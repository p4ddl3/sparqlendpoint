package sparql;

import java.util.concurrent.Callable;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

public class QueryRunner implements Callable<ResultSet>{

	private AbstractQueryExecution engine;
	private int total;
	private int current;
	public QueryRunner(AbstractQueryExecution engine, int current, int total){
		this.current = current;
		this.total = total;
		this.engine = engine;
	}
	@Override
	public ResultSet call() throws Exception {
		long time1 = System.currentTimeMillis(), ellaps;
		ResultSet set = engine.getQuery().execSelect();
		long time2 = System.currentTimeMillis();
		ellaps = time2-time1;
		System.out.println("query "+current+"/"+total+" executed in "+ellaps+" ms.");
		return set;
	}

}
