import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import model.EndPointLocation;
import model.ResultModel;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import util.Bundle;


public class EndPointPane extends JPanel implements Observer, ActionListener, MouseListener{
	private static final long serialVersionUID = 1L;

	//parent
	private EndPointFrame parent;
	
	//query execution
	private JPanel queryPanel;
	private JPanel queryControlPane;
	private JTabbedPane tabbedQueryPane;
	private RSyntaxTextArea queryPane;
	private RSyntaxTextArea errorPane;
	private JButton queryExecuteButton;
	private JButton previousQueryButton;
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
		
		
		queryPanel = new JPanel();
		queryPanel.setLayout(new BorderLayout());
		
		tabbedQueryPane = new JTabbedPane();
		
		
		queryPane = new RSyntaxTextArea();
		queryPane.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
		queryPane.setCodeFoldingEnabled(true);
		queryPane.setAntiAliasingEnabled(true);
		RTextScrollPane scrollp = new RTextScrollPane(queryPane);
		scrollp.setFoldIndicatorEnabled(true);
		tabbedQueryPane.add(scrollp, "Query");
		errorPane = new RSyntaxTextArea();
		errorPane.setEditable(false);
		errorPane.setForeground(Color.red);
		tabbedQueryPane.add(new RTextScrollPane(errorPane), "Errors");
		
		queryPanel.add(tabbedQueryPane, BorderLayout.CENTER);
		
		queryControlPane = new JPanel();
		queryControlPane.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx=1;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.weighty = 1;
		gbc.fill =  GridBagConstraints.HORIZONTAL;
		
		previousQueryButton = new JButton("previous");
		previousQueryButton.addActionListener(this);
		previousQueryButton.setEnabled(false);
		queryControlPane.add(previousQueryButton, gbc);
		
		queryExecuteButton = new JButton("Execute");
		queryExecuteButton.addActionListener(this);
		if(!location.isLoaded())
			queryExecuteButton.setEnabled(false);
		gbc.weighty = -1;
		queryControlPane.add(queryExecuteButton, gbc);
		
		queryPanel.add(queryControlPane, BorderLayout.EAST);
		queryPanel.setPreferredSize(new Dimension(200, 200));
		
		
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
		//splitPane
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.add(viewPanel);
		splitPane.add(queryPanel);
		add(splitPane, BorderLayout.CENTER);
		
	}


	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == queryExecuteButton){
			String query = queryPane.getText();
			resultModel.setResults(executor.pushQueryAndExec(query));
		}
		
		if(event.getSource() == previousQueryButton){
			executor.undo();
		}
		
	}


	@Override
	public void update(Observable arg0, Object arg1) {
		Bundle bundle = (Bundle) arg1;
		if(bundle.isNamedAs("queryExecutor")){
			QueryExecutor queryExec = (QueryExecutor) arg0;
			if(bundle.getValue("action").equals("error")){
				errorPane.setText(queryExec.getErrorMessage());
				tabbedQueryPane.setSelectedIndex(1);
				return;
			}
			int position = executor.getCurrentQueryPosition();
			if(position > 1)
				previousQueryButton.setEnabled(true);
			else
				previousQueryButton.setEnabled(false);
			
			if(bundle.getValue("action").equals("undo")){
				queryPane.setText(executor.getCurrentQuery());
			}
			return;
		}
		if(bundle.isNamedAs("endpointlocation")){
			if(location.isLoaded()){
				queryExecuteButton.setEnabled(true);
			}
		}
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
		queryPane.setText(query);
		resultModel.setResults(executor.pushQueryAndExec(query));
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
