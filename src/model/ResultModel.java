package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import util.Bundle;

import com.hp.hpl.jena.query.ResultSet;

public class ResultModel extends Observable{
	private List<ResultSet> results;
	
	public ResultModel(){
		results = new ArrayList<ResultSet>();
	}
	public void setResults(List<ResultSet> results){
		this.results = results;
		if(results == null){//error
			setChanged();
			notifyObservers(new Bundle("resultModel").put("action", "error"));
			return;
		}
		setChanged();
		notifyObservers(new Bundle("resultModel").put("action", "updated"));
	}
	public List<ResultSet> getResults(){
		return results;
	}
	
}
