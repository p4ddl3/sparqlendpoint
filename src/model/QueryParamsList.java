package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class QueryParamsList extends Observable{
	private List<AbstractQueryParam> params;
	
	public QueryParamsList(){
		params = new ArrayList<AbstractQueryParam>();
	}
	public void addParam(AbstractQueryParam param){
		this.params.add(param);
		setChanged();
		notifyObservers();
	}
	public List<AbstractQueryParam> getParams(){
		return params;
	}
	public int getQueryParamCount(){
		return params.size();
	}
	public void delParam(int idx){
		params.remove(idx);
		setChanged();
		notifyObservers();
	}
}
