package model;

public class AtomicQueryParam extends AbstractQueryParam{
	public static final String TYPE = "Atomic";
	public AtomicQueryParam( String name, String param) {
		super(AtomicQueryParam.TYPE, name);
		setValue("");
	}

	@Override
	public String renderValue() {
		return ((String)value).toString();
	}

}
