package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import model.QueryParamsList;

public class QueryEditorView extends JPanel implements Observer, ActionListener{

	private static final long serialVersionUID = 1L;
	private SparqlEditorPanel editorPanel;
	private QueryErrorPanel errorPanel;
	private QueryParamsPanel paramsPanel;
	
	private JTabbedPane tabbedPane;
	private EndPointPane parent;
	
	private List<String> viewName;
	
	public QueryEditorView(EndPointPane parent){
		this.parent = parent;
		viewName = new ArrayList<String>();
		setLayout(new BorderLayout());
		tabbedPane = new JTabbedPane();
		
		editorPanel = new SparqlEditorPanel(parent);
		tabbedPane.add(editorPanel, "Query");
		viewName.add("Query");
		
		paramsPanel = new QueryParamsPanel();
		tabbedPane.add(paramsPanel, "Parameters");
		viewName.add("Parameters");
		
		errorPanel = new QueryErrorPanel(parent.getResultModel());
		tabbedPane.add(errorPanel, "Errors");
		viewName.add("Errors");
		
		add(tabbedPane, BorderLayout.CENTER);
		setPreferredSize(new Dimension(200,200));
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	public String getQuery(){
		return editorPanel.getText();
	}
	public void setQuery(String query){
		editorPanel.setText(query);
	}
	public QueryParamsList getParamList(){
		return paramsPanel.getParamList();
	}
	public void setSelectedView(String name){
		int idx = viewName.indexOf(name);
		if(idx != -1)
			tabbedPane.setSelectedIndex(idx);
	}
	public void refresh(){
		editorPanel.refresh();
	}
	public QueryParamsPanel getParamsPanel(){
		return paramsPanel;
	}
}
