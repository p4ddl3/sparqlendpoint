package model;

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
				value = param.renderValue();
		}
		return value;
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
	public boolean isCellEditable(int rowIdw, int colIdx){
		if(colIdx == 0 || colIdx == 2 )
			return true;
		else
			return false;
	}
	public void setValueAt(Object value, int rowIndex, int columnIndex){
		AbstractQueryParam param = model.getParams().get(rowIndex);
		if(columnIndex == 0){//changement de nom
			String name = (String) value;
			param.setName(name);
		}
		if(columnIndex == 2){
			String str = (String) value;
			param.addValue(str);
		}
		refresh();
	}

}
