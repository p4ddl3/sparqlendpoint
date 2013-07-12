package view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;

import model.EndPointLocation;
import model.QueryParamsList;
import model.ResultModel;
import model.ResultViewModel;
import sparql.QueryExecutor;
import view.debug.QueryDebugFrame;
import view.editor.QueryEditorView;
import view.results.CellTablePopUp;
import view.results.FilterPanel;

import com.hp.hpl.jena.query.ResultSet;


public class EndPointPane extends JPanel implements Observer, ActionListener, MouseListener{
	private static final long serialVersionUID = 1L;

	//parent
	private EndPointFrame parent;
	
	private QueryEditorView queryEditor;
	//result view
	private JTable resultView;
	private ResultViewModel viewModel;
	private JPanel viewPanel;
	private FilterPanel filter;
	
	private QueryExecutor executor;
	private ResultModel resultModel;
	private EndPointLocation location;
	private String name;
	
	public EndPointPane(EndPointFrame parent, String name) {
		this.parent = parent;
		this.name = name;
		resultModel = new ResultModel();
		location = this.parent.getEndPointLocation();
		location.addObserver(this);
		executor = new QueryExecutor(location);
		executor.addObserver(this);
		this.setLayout(new BorderLayout());
		
		
		viewModel = new ResultViewModel();
		viewModel.setModel(resultModel);
		resultView = new JTable(viewModel);
		resultView.addMouseListener(this);
		resultView.setInheritsPopupMenu(true);
		JScrollPane scroll = new JScrollPane(resultView);
		
		viewPanel = new JPanel();
		viewPanel.setLayout(new BorderLayout());
		viewPanel.add(scroll, BorderLayout.CENTER);
		filter = new FilterPanel(this,viewModel);
		viewPanel.add(filter, BorderLayout.SOUTH);
		
		
		queryEditor = new QueryEditorView(this);
		
		//splitPane
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.add(viewPanel);
		splitPane.add(queryEditor);
		add(splitPane, BorderLayout.CENTER);
		
	}


	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getActionCommand().equals("editor.query.execute")){
			String query = queryEditor.getQuery();
			QueryParamsList params = queryEditor.getParamList();
			List<ResultSet> results =executor.pushQueryAndExec(query, params);
			if(results != null){
				resultModel.setResults(results);
			}else{
				resultModel.setErrorMessage(executor.getErrorMessage());
				queryEditor.setSelectedView("Errors");
			}
			return; 
		}
		if(event.getActionCommand().equals("editor.query.show")){
			QueryDebugFrame dbg = new QueryDebugFrame(this);
			dbg.setVisible(true);
			return;
		}
	}


	@Override
	public void update(Observable arg0, Object arg1) {
		queryEditor.refresh();
	}
	public String getName(){
		return this.name;
	}
	public EndPointFrame getFrame(){
		return parent;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int col = resultView.getSelectedColumn();
		int row = resultView.getSelectedRow();
		if( 		col >= 0 && col < resultView.getColumnCount()
				&& 	row >= 0 && row < resultView.getRowCount()){
			String str = viewModel.getValueAt(row, col).toString();
			CellTablePopUp popup = new CellTablePopUp(this,str);
			popup.show(e.getComponent(),e.getX(), e.getY());
		}
	}
	public void setQueryAndRun(String query){
		queryEditor.setQuery(query);
		resultModel.setResults(executor.pushQueryAndExec(query, null));
	}
	public void showPanel(boolean aFlag){
		if(aFlag)
			filter.activate();
		else
			filter.desactivate();
	}
	public ResultViewModel getViewModel(){
		return viewModel;
	}
	public ResultModel getResultModel(){
		return resultModel;
	}
	public EndPointLocation getEndPointLocation(){
		return location;
	}
	public QueryEditorView getEditor(){
		return queryEditor;
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}
	@Override
	public void mouseReleased(MouseEvent e) {
	}
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	@Override
	public void mouseExited(MouseEvent e) {
	}
}
