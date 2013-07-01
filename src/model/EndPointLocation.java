package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Observable;

import sparql.AbstractQueryExecution;
import util.Bundle;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class EndPointLocation extends Observable{
	private String url;
	private boolean remote;
	private boolean loaded;
	private Model localModel;
	public EndPointLocation(){
	}
	public void setRemote(boolean remote){
		this.remote = remote;
	}
	public void setURL(String url){
		this.url = url;
	}
	public String getURL(){
		return url;
	}
	public boolean isRemote(){
		return remote;
	}
	public void load(){
		if(!remote){
			 File file = new File(url);
			 try {
				InputStream in = new FileInputStream(file);
				localModel = ModelFactory.createMemModelMaker().createDefaultModel();
				localModel.read(in, null);
				in.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		loaded = true;
		setChanged();
		notifyObservers(new Bundle("endpointlocation"));
	}
	public boolean isLoaded(){
		return loaded;
	}
	public AbstractQueryExecution getAbstractQueryExecution(Query query){
		AbstractQueryExecution aqe = new AbstractQueryExecution();
		if(remote){
			aqe.setAbstractQueryExecution(QueryExecutionFactory.createServiceRequest(url, query));
		}else{
			aqe.setAbstractQueryExecution(QueryExecutionFactory.create(query, localModel));
		}
		return aqe;
	}
}
