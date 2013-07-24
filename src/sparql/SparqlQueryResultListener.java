package sparql;

import java.util.List;

import com.hp.hpl.jena.query.ResultSet;

public interface SparqlQueryResultListener {
	public void onSparqlQueryResult(List<ResultSet> results);
}
