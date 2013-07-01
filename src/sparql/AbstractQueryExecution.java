package sparql;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

public class AbstractQueryExecution {
	private  QueryExecution qe;
	public void setAbstractQueryExecution(QueryExecution qe){
		this.qe = qe;
	}
	public void addParam(String key, String value){
		if(qe instanceof QueryEngineHTTP)
			((QueryEngineHTTP) qe).addParam(key, value);
		else
			System.err.println("adding param on local model isn't supported.");
	}
	public QueryExecution getQuery(){
		return qe;
	}
}
