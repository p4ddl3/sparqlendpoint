package sparql;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.EndPointLocation;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;

public class SparqlQueryExecutor {
	public static final int FLAG_LITERAL = 0;
	public static final int FLAG_USING_NAMESPACE = 1;
	
	private String queryString;
	private String queryName;
	private EndPointLocation location;
	private String errorMessage;
	private Map<String,String> params;
	private List<AbstractQueryExecution> engines;
	private int charMax;
	private Pattern listPattern = Pattern.compile("#EXPAND_OR\\[#(?<name>.*?)\\]\\[SEQUABLE=(?<sequable>.*?)\\]\\[(?<constant>.*?) = (?<value>#VALUE)\\]");
	private Pattern seqSequence = Pattern.compile("#SEQUABLE\\[(?<sequence>.*?)\\]");
	
	
	public SparqlQueryExecutor(EndPointLocation location, SparqlQueryProvider provider, int charMax) {
		engines = new ArrayList<AbstractQueryExecution>();
		this.charMax = charMax;
		this.location = location;
		queryName = "query";
		queryString = provider.getQuery();
		params = new HashMap<String,String>();
		errorMessage = "";
	}
	
	public void addParam(String key, String value){
		params.put(key, value);
	}
	public void setValue(String key, String value){
		if(key.startsWith("$"))
			key = "\\"+key;
		queryString = queryString.replaceAll(key, value);
	}
	public void expandList(String name, List<String> values, int flag){
		Matcher matcher = listPattern.matcher(queryString);
		boolean found = false;
			while(matcher.find()){
				if(matcher.group("name").equals(name)){
					found = true;
					break;
				}
			}
			if(found){
				String repl = "";
				int start = matcher.start();
				int end = matcher.end();
				String constant = matcher.group("constant");
				boolean sequable = Boolean.parseBoolean(matcher.group("sequable"));
				int i = 0 ;
				for(i = 0; i < values.size()-1; i++){
					if(flag == FLAG_LITERAL) 		repl += constant + " = <" + values.get(i) + "> || ";
					if(flag == FLAG_USING_NAMESPACE)repl += constant + " = " + values.get(i) + " || ";
				}
				if(flag == FLAG_LITERAL) 		repl += constant + " = <" + values.get(i)+"> ";
				if(flag == FLAG_USING_NAMESPACE)repl += constant + " = " + values.get(i)+" ";	
				String toBeRepl = queryString.substring(start, end);
				if(sequable)
					queryString = queryString.replace(toBeRepl,"#SEQUABLE["+ repl+"]");
				else
					queryString = queryString.replace(toBeRepl,repl);
			}else{
				System.err.println(name + " not found.");
			}
	}
	public List<ResultSet> execute(){
		long globalTime = System.currentTimeMillis();
		long ellaps;
		Map<String,Integer> map = querySplit();
		List<String> queries = new ArrayList<String>();
		for(String str : map.keySet())
			queries.add(str);
		List<ResultSet> results = new ArrayList<ResultSet>();
		long localTime;
		long localTime2;
		for(int i =0; i < queries.size(); i++){
			print("part("+(i+1)+"/"+queries.size()+") : envoi de "+((map.get(queries.get(i))!=-1)?map.get(queries.get(i))+ " references...": "la requete..."));
			localTime = System.currentTimeMillis();
			//System.out.println(queries.get(i));
			try{
			QueryFactory.parse(new Query(), queries.get(i), "", Syntax.syntaxSPARQL_11);
			}catch(Exception qpe){
				errorMessage = qpe.getMessage();
				return null;
			}
			Query query = QueryFactory.create(queries.get(i));
			AbstractQueryExecution	qexec =location.getAbstractQueryExecution(query);
			for(String str : params.keySet()){
				qexec.addParam(str, params.get(str));
			}
			/*TimeOutChecker checker = new TimeOutChecker();
			checker.run();
			checker.interrupt();*/
			ResultSet set = qexec.getQuery().execSelect();
			engines.add(qexec);
			results.add(set);
			//qexec.close();
			localTime2 = System.currentTimeMillis();
			ellaps = localTime2-localTime;
			print("part("+(i+1)+"/"+queries.size()+") : terminée (" + ellaps + " ms)");
		}
		long globalTime2 = System.currentTimeMillis();
		ellaps = globalTime2-globalTime;
		print("terminée (" + ellaps + " ms)");
		return  results;
	}
	private Map<String,Integer> querySplit(){
		Map<String,Integer> queries = new HashMap<String,Integer>();
		Matcher matcher = seqSequence.matcher(queryString);
		if(queryString.length() < charMax){//petite requete, on supprime les sequables et onn return direct.
			boolean found = false;
			while(matcher.find()){
				found = true;
				int start = matcher.start();
				int end = matcher.end();
				String fullSeq = matcher.group("sequence");
				String toBeRepl = queryString.substring(start, end);
				queryString = queryString.replace(toBeRepl, fullSeq);
				queries.put(queryString,fullSeq.split("\\|\\|").length);
			}
			if(!found)//il n'y a pas de EXPAND
				queries.put(queryString,-1);
		}else{//grosse requete
			if(!matcher.find()){
				print("requete trop volumineuse (> "+charMax+" chars). Il faut la découper (via SEQUABLE=true par exemple).");
				return null;
			}
			//requete trop grosse, il faut la découper
			else{
				String fullSeq = matcher.group("sequence");
				int start = matcher.start();
				int end = matcher.end();
				String templateBegin = queryString.substring(0, start);
				String templateEnd = queryString.substring(end);
				String[] values = fullSeq.split("\\|\\|");
				int queryBaseLen = templateBegin.length() + templateEnd.length();
				int i =0;
				StringBuilder tmpQuery;
				int tmp;
				while(i < values.length){
					int len = queryBaseLen;
					tmpQuery = new StringBuilder(templateBegin);
					tmp = 0;
					while(i < values.length && (len + values[i].length()+4) < charMax){//prendre les || en compte !!
						tmpQuery.append(values[i]).append(" || ");
						len += values[i].length() +4;
						i++;
						tmp++;
					}
					
					tmpQuery.delete(tmpQuery.length()-3,tmpQuery.length() );
					tmpQuery.append(templateEnd);
					queries.put(tmpQuery.toString(), tmp);
				}
			}
			
		}
		
		return queries;
	}
	private void print(String str){
		System.out.println("["+queryName + "] "+str);
	}
	public void release(){
		for(AbstractQueryExecution qexec : engines)
			qexec.getQuery().close();
	}
	public String getQueryString(){
		return queryString;
	}
	public String getErrorMessage(){
		return errorMessage;
	}

}
