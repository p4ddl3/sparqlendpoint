import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Observable;
import java.util.Observer;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;


public class ModelTarget extends Observable{
	private Model model;
	private ResultSet currentResult;
	private boolean loaded;
	private String url;
	private boolean remote;
	private String errorMessage;
	
	public ModelTarget(){
		errorMessage = "";
	}
	public void load(String url){
		this.url = url;
		if(!remote){
			
			File file = new File(url);
			try {
				InputStream in = new FileInputStream(file);
				model = ModelFactory.createMemModelMaker().createDefaultModel();
				model.read(in, null);
				in.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		loaded = true;
		currentResult = null;
		setChanged();
		notifyObservers();
	}
	
	public void executeQuery(String queryString){
		errorMessage = "";
		Query q = new Query();
		try{
		QueryFactory.parse(q, queryString, "", Syntax.syntaxSPARQL_11);
		}catch(Exception e){
			errorMessage = e.getMessage();
		}
		if(errorMessage.equals("")){//si il n'y a pas d'erreur syntaxique
			if(!remote){//fichier local
				QueryExecution qe = QueryExecutionFactory.create(q, model);
				currentResult = qe.execSelect();
			}
			else{//base distante
				QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest(url, q);
				currentResult = qexec.execSelect();
			}
		}
		setChanged();
		notifyObservers();
	}
	public ResultSet getResultSet(){
		return currentResult;
	}
	public String getErrorMessage(){
		return errorMessage;
	}

	public boolean isReady() {
		return loaded;
	}

	public void addObs(Observer observer) {
		addObserver(observer);
		
	}

	public String getURL() {
		return url;
	}
	public void setRemote(boolean remote){
		this.remote = remote;
	}
	public boolean isRemote(){
		return remote;
	}

}
