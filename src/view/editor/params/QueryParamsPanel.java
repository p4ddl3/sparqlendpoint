package view.editor.params;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import model.AbstractQueryParam;
import model.AtomicQueryParam;
import model.ListedQueryParam;
import model.QueryParamFactory;
import model.QueryParamsList;
import model.QueryParamsViewModel;

public class QueryParamsPanel extends JPanel implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	private JTable table;
	private QueryParamsViewModel viewModel;
	private QueryParamsList model;
	
	private JPanel bottomPane;
	private JButton addButton;
	private JButton delButton;
	
	
	public QueryParamsPanel(){
		model = new QueryParamsList();
		viewModel = new QueryParamsViewModel();
		//table = new JTable(viewModel);
		table = new ParamsTable(viewModel);
		viewModel.setModel(model);
		setLayout(new BorderLayout());
		JScrollPane scroll = new JScrollPane(table);
		
		bottomPane = new JPanel();
		addButton = new JButton("add parameter");
		addButton.addActionListener(this);
		delButton = new JButton("delete parameter");
		delButton.addActionListener(this);
		
		bottomPane.add(addButton);
		bottomPane.add(delButton);
		
		add(scroll, BorderLayout.CENTER);
		add(bottomPane, BorderLayout.SOUTH);
	}
	public QueryParamsList getParamList(){
		return model;
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == delButton){
			int idx = table.getSelectedRow();
			if( idx != -1)
				model.delParam(idx);
			return;
		}
		if(event.getSource() == addButton){
			Object[] possibilities = {AtomicQueryParam.TYPE, ListedQueryParam.TYPE};
			String s = (String)JOptionPane.showInputDialog(
			                    this,
			                    "Select parameter type :",
			                    "New Parameter",
			                    JOptionPane.PLAIN_MESSAGE,
			                    null,
			                    possibilities,
			                    AtomicQueryParam.TYPE);
			AbstractQueryParam param = QueryParamFactory.create(s);
			if(param != null){
				model.addParam(param);
			}
			return;
		}
		
	}
	public JTable getParamView(){
		return table;
	}
}
