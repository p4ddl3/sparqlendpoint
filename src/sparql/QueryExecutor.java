package sparql;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Stack;

import view.StatusBar;

import model.AbstractQueryParam;
import model.AtomicQueryParam;
import model.EndPointConfig;
import model.EndPointStore;
import model.ListedQueryParam;
import model.QueryParamsList;

import com.hp.hpl.jena.query.ResultSet;


public class QueryExecutor extends Observable implements  SparqlQueryResultListener{
	private Stack<String> queries;
	private SparqlQueryExecutor queryExec;
	private String errorMessage;
	private List<ResultSet> results;
	public QueryExecutor(){
		queries = new Stack<String>();
		errorMessage = "";
	}
	@SuppressWarnings("unchecked")
	public void pushQueryAndExec(String query, QueryParamsList params){
		
		queries.push(query);
		queryExec = new SparqlQueryExecutor(EndPointStore.get().getSelectedConfig(), new SparqlQueryFromString(query), this);
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
		List<String> reserved = new ArrayList<String>();
		reserved.add("charmax");
		
		if(config.isRemote()){
			for(String key : config.getParams().keySet()){
				if(!reserved.contains(key)){
					System.out.println("addParam " + key +" = " + config.getParams().get(key));
					queryExec.addParam(key, config.getParams().get(key));
				}
			}
		}
		queryExec.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("progress".equals(evt.getPropertyName())) {
			         StatusBar.get().publishProgress((Integer)evt.getNewValue());
			        }
			}
		});
		StatusBar.get().out("Executing query...");
		StatusBar.get().publishProgress(0);
		queryExec.execute();
		
		
	}
	public void undo(){
		if (queries.size() >1)
			queries.pop();
		setChanged();
		notifyObservers("queryExecutor.action.undo");
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
	@Override
	public void onSparqlQueryResult(List<ResultSet> results) {
		this.results = results;
		if(results == null){
			errorMessage = queryExec.getErrorMessage();
			setChanged();
			notifyObservers("queryExecutor.result.error");
			StatusBar.get().err("There are some errors in the query. Please check errors view.");
		}else{
			setChanged();
			notifyObservers("queryExecutor.result.success");
			StatusBar.get().out("Query succesfully executed", 3000);
		}
		StatusBar.get().publishProgress(0);
	}
	public List<ResultSet> getResults(){
		return results;
	}
}
