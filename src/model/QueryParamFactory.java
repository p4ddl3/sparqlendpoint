package model;

public class QueryParamFactory {
	public static AbstractQueryParam create(String type){
		AbstractQueryParam queryParam = null;
		switch(type){
		case AtomicQueryParam.TYPE:
			queryParam = new AtomicQueryParam("type name", "type value");
			break;
		}
		return queryParam;
	}
}
