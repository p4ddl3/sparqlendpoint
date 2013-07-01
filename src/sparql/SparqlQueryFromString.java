package sparql;

public class SparqlQueryFromString implements SparqlQueryProvider{
	private String queryString;
	public SparqlQueryFromString(String query){
		this.queryString = query;
	}
	@Override
	public String getQuery() {
		return this.queryString;
	}

}
