package model;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractQueryParam {
	protected String type;
	protected String name;
	protected List<String> values;
	public AbstractQueryParam(String type, String name){
		this.type = type;
		this.name = name;
		values = new ArrayList<String>();
	}
	public void addValue(String value){
		values.add(value);
	}
	
	public String getType(){
		return type;
	}
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
	public void setType(String type){
		this.type = type;
	}
	public abstract String renderValue();
}
