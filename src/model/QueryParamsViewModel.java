package model;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.table.AbstractTableModel;

public class QueryParamsViewModel extends AbstractTableModel implements Observer{
	private QueryParamsList model;
	private static final long serialVersionUID = 1L;
	private String[] columnNames;
	public QueryParamsViewModel(){
		columnNames = new String[]{"Parameter Name", "Parameter type","Parameter value"};
	}
	
	public boolean isCellEditable(int row, int col){
		if(col == 0 || col == 2){
			return true;
		}
		return false;
	}
	@Override
	public int getRowCount() {
		return model.getQueryParamCount();
	}
	
	public String getColumnName(int col) {
	      return columnNames[col];
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		AbstractQueryParam param = model.getParams().get(rowIndex);
		String value = "";
		switch(columnIndex){
			case 0:
				value = param.getName();
				break;
			case 1:
				value = param.getType()+"";
				break;
			case 2 :
				return param.getValue();
		}
		return value;
	}
	@SuppressWarnings("unchecked")
	public void setValueAt(Object value, int row, int col){
		AbstractQueryParam param = model.getParams().get(row);
		switch(col){
			case 0:
				param.setName((String) value);
				break;
			case 2:
				switch(param.getType()){
					case AtomicQueryParam.TYPE:
						param.setValue((String) value);
						break;
					case ListedQueryParam.TYPE:
						param.setValue((List<String>) value);
						break;
				}
				break;
		}
	}
	public void refresh(){
		fireTableDataChanged();
		fireTableStructureChanged();
	}
	@Override
	public void update(Observable o, Object arg) {
		refresh();
		
	}
	public void setModel(QueryParamsList model){
		this.model = model;
		model.addObserver(this);
		refresh();
	}

}
