import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.table.AbstractTableModel;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;


public class ResultViewModel extends AbstractTableModel implements Observer{
	
	private static final long serialVersionUID = 1L;
	private  ModelTarget model;
	private String[] columnNames;
	private ArrayList<String[]> data;
	public ResultViewModel() {
		columnNames = new String[]{"Execute", "Your","Query!"};
		data = new ArrayList<String[]>();
	}

	 public String getColumnName(int col) {
	      return columnNames[col];
	    }
	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return data.size();
	}

	public Object getValueAt(int x, int y) {
		return data.get(x)[y];
	}

	public void update(Observable arg0, Object arg1) {
		refresh();
	}
	public void setModel(ModelTarget model){
		this.model = model;
		this.model.addObs(this);
		refresh();
	}
	public void refresh(){
		data = new ArrayList<String[]>();
		ResultSet set = model.getResultSet();
		if(set != null){
			int nbCol = set.getResultVars().size();
			columnNames = new String[nbCol];
			for(int i = 0; i < nbCol; i++){
				columnNames[i] = set.getResultVars().get(i);
			}
			while(set.hasNext()){
				QuerySolution info = set.next();
				String[] line = new String[nbCol];
				for(int j = 0; j < nbCol; j++){
					RDFNode content = info.get(columnNames[j]);
					line[j] = (content != null)?content.toString():"";
				}
				data.add(line);
			}
		}
		fireTableDataChanged();
		fireTableStructureChanged();
	}
	public int getNbLines(){
		return data.size();
	}

}