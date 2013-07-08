package model;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.table.AbstractTableModel;


import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.RDFNode;


public class ResultViewModel extends AbstractTableModel implements Observer{
	
	private static final long serialVersionUID = 1L;
	private String[] columnNames;
	private ArrayList<String[]> data;
	private List<QuerySolution>list;
	private ResultModel model;
	private String filter;
	public ResultViewModel() {
		columnNames = new String[]{"Execute", "Your","Query!"};
		data = new ArrayList<String[]>();
		list = new ArrayList<QuerySolution>();
		filter= null;
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
	public void setModel(ResultModel model){
		this.model = model;
		this.model.addObserver(this);
		refresh();
	}
	public void refresh(){
		data = new ArrayList<String[]>();
		if(model.getResults() != null && model.getResults().size() >= 1){
			list = model.getResults();
			columnNames = model.getColumnNames();
			int nbCol = model.getColumnNames().length;
				for(QuerySolution info : list){
					String[] line = new String[nbCol];
					for(int j = 0; j < nbCol; j++){
						RDFNode content = info.get(columnNames[j]);
						line[j] = (content != null)?content.toString():"";
					}
					if(filter == null)
						data.add(line);
					else{
						boolean isValid = false;
						for(String field : line){
							isValid = false;
							if(field.contains(filter)){
								isValid = true;
								break;
							}
						}
						if(isValid){
							data.add(line);
						}
					}
				}
			
			fireTableDataChanged();
			fireTableStructureChanged();
		}
	}
	public int getNbLines(){
		return data.size();
	}
	public int getNbFullLines(){
		return list.size();
	}
	public String getFilter(){
		return filter;
	}
	public void setFilter(String filter){
		this.filter = filter;
		refresh();
	}

}
