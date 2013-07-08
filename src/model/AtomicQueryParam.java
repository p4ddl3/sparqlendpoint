package model;

public class AtomicQueryParam extends AbstractQueryParam{
	public static final String TYPE = "Atomic";
	public AtomicQueryParam( String name, String param) {
		super(AtomicQueryParam.TYPE, name);
		addValue(param);
	}
	public void addValue(String param){
		this.values.clear();
		this.values.add(param);
	}

	@Override
	public String renderValue() {
		return this.values.get(0).toString();
	}

}
