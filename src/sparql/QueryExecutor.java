package sparql;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Stack;

import com.hp.hpl.jena.query.ResultSet;

import model.AbstractQueryParam;
import model.AtomicQueryParam;
import model.EndPointConfig;
import model.EndPointStore;
import model.ListedQueryParam;
import model.QueryParamsList;

import util.Bundle;


public class QueryExecutor extends Observable{
	private Stack<String> queries;
	private SparqlQueryExecutor queryExec;
	private String errorMessage;
	public QueryExecutor(){
		queries = new Stack<String>();
		errorMessage = "";
	}
	@SuppressWarnings("unchecked")
	public List<ResultSet> pushQueryAndExec(String query, QueryParamsList params){
		List<ResultSet> results = new ArrayList<ResultSet>();
		queries.push(query);
		queryExec = new SparqlQueryExecutor(EndPointStore.get().getSelectedConfig(), new SparqlQueryFromString(query));
		if(params != null){
			for(AbstractQueryParam param : params.getParams()){
				System.out.println(param);
				if(param.getType() == AtomicQueryParam.TYPE){
					queryExec.setValue(param.getName(), param.renderValue());
				}
				if(param.getType() == ListedQueryParam.TYPE){
					System.out.println("list");
					queryExec.expandList(param.getName(), (List<String>)param.getValue(), SparqlQueryExecutor.FLAG_USING_NAMESPACE);
				}
			}
			
		}
		System.out.println(params);
		
		EndPointConfig config  = EndPointStore.get().getSelectedConfig();
		
		if(config.isRemote()){
			for(String key : config.getParams().keySet()){
				queryExec.addParam(key, config.getParams().get(key));
			}
		}
		results = queryExec.execute();
		System.out.println(queryExec.getQueryString());
		if(results == null){
			errorMessage = queryExec.getErrorMessage();
			setChanged();
			notifyObservers(new Bundle("queryExecutor").put("action", "error"));
		}else{
			setChanged();
			notifyObservers(new Bundle("queryExecutor").put("action", "push"));
		}
		return results;
	}
	public void undo(){
		if (queries.size() >1)
			queries.pop();
		setChanged();
		notifyObservers(new Bundle("queryExecutor").put("action", "undo"));
	}
	public int getCurrentQueryPosition(){
		return queries.size();
	}
	public String getCurrentQuery(){
		return queries.peek();
	}
	public String getErrorMessage(){
		return errorMessage;
	}
}
