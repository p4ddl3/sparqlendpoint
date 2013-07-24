package sparql;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingWorker;

import model.EndPointConfig;



import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

public class SparqlQueryExecutor extends SwingWorker<List<ResultSet>, Integer>{
	public static final int FLAG_LITERAL = 0;
	public static final int FLAG_USING_NAMESPACE = 1;
	
	private String queryString;
	private String queryName;
	private EndPointConfig location;
	private String errorMessage;
	private Map<String,String> params;
	private List<AbstractQueryExecution> engines;
	private int charMax;
	private Pattern listPattern = Pattern.compile("#EXPAND_OR\\[#(?<name>.*?)\\]\\[SEQUABLE=(?<sequable>.*?)\\]\\[(?<constant>.*?) = (?<value>#VALUE)\\]");
	private Pattern seqSequence = Pattern.compile("#SEQUABLE\\[(?<sequence>.*?)\\]");
	private List<ResultSet> results;
	private SparqlQueryResultListener callback;
	
	public SparqlQueryExecutor(EndPointConfig location, SparqlQueryProvider provider, SparqlQueryResultListener callback) {
		engines = new ArrayList<AbstractQueryExecution>();
		this.charMax = location.getCharMax();
		System.out.println("charmax="+charMax);
		this.location = location;
		queryName = "query";
		queryString = provider.getQuery();
		params = new HashMap<String,String>();
		errorMessage = "";
		this.callback = callback;
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
	public Map<String,Integer> querySplit(){
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

	@Override
	protected List<ResultSet> doInBackground() throws Exception {
		long globalTime = System.currentTimeMillis(), ellaps;
		Map<String,Integer> map = querySplit();
		List<String> queries = new ArrayList<String>();
		for(String str : map.keySet())
			queries.add(str);
		for(int i =0; i < queries.size(); i++){
			try{
			QueryFactory.parse(new Query(), queries.get(i), "", Syntax.syntaxSPARQL_11);
			}catch(Exception qpe){
				errorMessage = qpe.getMessage();
				results = null;
			}
			Query query = QueryFactory.create(queries.get(i));
			AbstractQueryExecution	qexec =location.getAbstractQueryExecution(query);
			for(String str : params.keySet()){
				qexec.addParam(str, params.get(str));
			}
			engines.add(qexec);
			
		}
		results = executeInParallel(engines);
		long globalTime2 = System.currentTimeMillis();
		ellaps = globalTime2-globalTime;
		print("terminée (" + ellaps + " ms)");
		return results;
	}
	protected void done(){
		if(callback != null)
			callback.onSparqlQueryResult(results);
	}
	public List<ResultSet> executeInParallel(List<AbstractQueryExecution> engines){
		List<ResultSet> sets = null;
		long globalTime = System.currentTimeMillis(), ellaps;
		List<QueryRunner> runners = new ArrayList<QueryRunner>();
		for(int i = 0; i < engines.size(); i++){
			runners.add(new QueryRunner(engines.get(i), i, engines.size()));
		}
		
		ExecutorService executor = Executors.newFixedThreadPool(20);
		try {
			sets = resolve(executor, runners);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		executor.shutdown();
		long globalTime2 = System.currentTimeMillis();
		ellaps = globalTime2-globalTime;
		return sets;
	}
	public List<ResultSet> resolve(ExecutorService executor, List<QueryRunner> runners) throws Exception{
		CompletionService<ResultSet> completionService = new ExecutorCompletionService<ResultSet>(executor);
		List<Future<ResultSet>> futures = new ArrayList<Future<ResultSet>>();
		List<ResultSet> list = new ArrayList<ResultSet>();
		for(QueryRunner runner : runners)
			futures.add(completionService.submit(runner));
		
		ResultSet set = null;
		for(int i =0; i < runners.size(); i++){
			set = completionService.take().get();
			setProgress((int)(i*100)/runners.size());
			if(set != null)
				list.add(set);
		}
		return list;
	}

}
