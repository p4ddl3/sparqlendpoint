package sparql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SparqlQueryFromFile implements SparqlQueryProvider{
	private String queryString;
	public SparqlQueryFromFile(String path){
		queryString = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(path)));
			String line = "";
			while ((line = br.readLine())!=null){
				queryString+=line;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getQuery() {
		return queryString;
	}
}
