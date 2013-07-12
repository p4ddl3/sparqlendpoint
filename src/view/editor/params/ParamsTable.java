package view.editor.params;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import model.QueryParamsViewModel;

public class ParamsTable extends JTable{
	private static final long serialVersionUID = 1L;
	
	public ParamsTable(QueryParamsViewModel model){
		super(model);
	}
	@SuppressWarnings("unchecked")
	public TableCellEditor getCellEditor(int row, int col){
		if(col != 2)
			return super.getCellEditor(row,col);
		if(getModel().getValueAt(row, col).getClass() != ArrayList.class){
			return super.getCellEditor(row, col);
		}
		DialogParamList dialog = new DialogParamList((List<String>)getModel().getValueAt(row, col));
		dialog.setVisible(true);
		return super.getCellEditor(row, col);
	}


}
