package model;


public abstract class AbstractQueryParam {
	protected String type;
	protected String name;
	protected Object value;
	public AbstractQueryParam(String type, String name){
		this.type = type;
		this.name = name;
	}
	public void setValue(Object obj){
		value = obj;
	}
	public Object getValue(){
		return value;
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
