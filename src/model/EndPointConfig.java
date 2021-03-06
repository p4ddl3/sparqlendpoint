package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import sparql.AbstractQueryExecution;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class EndPointConfig extends Observable{
	private String url;
	private boolean remote;
	private boolean loaded;
	private Model localModel;
	private Map<String, String> params;
	private String name;
	private List<String> prefixes;
	public EndPointConfig(){
		prefixes = new ArrayList<String>();
		params = new HashMap<String, String>();
		params.put("charmax", "1000");
	}
	public List<String> getPrefixes(){
		return prefixes;
	}
	public void addPrefix(String prefix){
		prefixes.add(prefix);
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
		notifyObservers("endpointConfig.location.loaded");
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
	public Map<String, String> getParams(){
		return params;
	}
	public void clearParams(){
		params.clear();
	}
	public void setParams(String key, String value){
		params.put(key,  value);
	}
	public int getCharMax(){
		int charmax =	Integer.parseInt(params.get("charmax"));
		return charmax;
	}
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
}
