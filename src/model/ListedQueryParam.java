package model;

import java.util.ArrayList;
import java.util.List;

public class ListedQueryParam extends AbstractQueryParam{

	public static final String TYPE= "List";
	
	public ListedQueryParam(String name) {
		super(ListedQueryParam.TYPE, name);
		setValue(new ArrayList<String>());
	}

	@Override
	public String renderValue() {
		@SuppressWarnings("unchecked")
		List<String> list = (List<String>)value;
		if(list.isEmpty()){
			return "(empty list)";
		}
		String str= "(";
		for(int i = 0; i < list.size()-1; i++){
			str += list.get(i)+", ";
		}
		str += list.get(list.size()-1)+")";
		return str;
	}

}
