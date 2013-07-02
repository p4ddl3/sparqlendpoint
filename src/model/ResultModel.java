package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import util.Bundle;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class ResultModel extends Observable{
	private List<QuerySolution> results;
	private String[] columnNames;
	public ResultModel(){
	}
	public void setResults(List<ResultSet> results){
		this.results = new ArrayList<QuerySolution>();
		if(results == null){//error
			setChanged();
			notifyObservers(new Bundle("resultModel").put("action", "error"));
			return;
		}
		for(ResultSet set : results){
			while(set.hasNext()){
				this.results.add(set.nextSolution());
			}
		}
		int nbCol = results.get(0).getResultVars().size();
		columnNames = new String[nbCol];
		for(int i = 0; i < nbCol; i++){
			columnNames[i] = results.get(0).getResultVars().get(i);
		}
		setChanged();
		notifyObservers(new Bundle("resultModel").put("action", "updated"));
	}
	public List<QuerySolution> getResults(){
		return results;
	}
	public String[] getColumnNames(){
		return columnNames;
	}
	
}
