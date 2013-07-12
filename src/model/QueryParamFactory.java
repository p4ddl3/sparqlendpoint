package model;

public class QueryParamFactory {
	public static AbstractQueryParam create(String type){
		AbstractQueryParam queryParam = null;
		if(type == null)
			return null;
		switch(type){
			case AtomicQueryParam.TYPE:
				queryParam = new AtomicQueryParam("key", "value");
				break;
			case ListedQueryParam.TYPE:
				queryParam = new ListedQueryParam("list");
				break;
		}
		return queryParam;
	}
}
