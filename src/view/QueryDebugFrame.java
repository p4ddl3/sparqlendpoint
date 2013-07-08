package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;

import model.AbstractQueryParam;
import model.AtomicQueryParam;
import model.QueryParamsList;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rtextarea.RTextScrollPane;

import sparql.SparqlQueryExecutor;
import sparql.SparqlQueryFromString;

public class QueryDebugFrame extends JFrame implements ActionListener, MouseListener{
	private static final long serialVersionUID = 1L;
	private RSyntaxTextArea queryPanel;
	private JPanel contentPane;
	private EndPointPane parent;
	private JButton refreshButton;
	private JPanel bottomPane;
	private JTable controller;
	public QueryDebugFrame(EndPointPane parent){
		super("Query Debug");
		this.parent = parent;
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout());
		queryPanel = new RSyntaxTextArea();
		AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory)TokenMakerFactory.getDefaultInstance();
		atmf.putMapping("text/sparql", "syntax.SparqlTokenMaker");
		queryPanel.setSyntaxEditingStyle("text/sparql");
		queryPanel.setEditable(false);
		queryPanel.setPreferredSize(new Dimension(400,400));
		RTextScrollPane queryPanelScroll = new RTextScrollPane(queryPanel);
		contentPane.add(queryPanelScroll, BorderLayout.CENTER);
		
		
		bottomPane = new JPanel();
		refreshButton = new JButton("refresh");
		refreshButton.addActionListener(this);
		bottomPane.add(refreshButton);
		contentPane.add(bottomPane, BorderLayout.SOUTH);
		
		controller = parent.getEditor().getParamsPanel().getParamView();
		controller.addMouseListener(this);
		init();
		pack();
	}
	public void init(){
		String query = parent.getEditor().getQuery();
		QueryParamsList list = parent.getEditor().getParamList();
		SparqlQueryExecutor executor = new SparqlQueryExecutor(parent.getEndPointLocation(), new SparqlQueryFromString(query), 1000);
		if(list != null){
			for(AbstractQueryParam param : list.getParams()){
				if(param.getType() == AtomicQueryParam.TYPE){
					executor.setValue(param.getName(), param.renderValue());
				}
			}
		}
		queryPanel.setText(executor.getQueryString());
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		init();
		
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		int idx = controller.getSelectedRow();
		if(idx >= 0){
			AbstractQueryParam param = parent.getEditor().getParamList().getParams().get(idx);
			String toSearch = param.renderValue();
			queryPanel.markAll(toSearch, false, false, false);
		}
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
