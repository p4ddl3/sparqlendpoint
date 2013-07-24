package view.debug;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import model.AbstractQueryParam;
import model.AtomicQueryParam;
import model.EndPointConfig;
import model.EndPointStore;
import model.ListedQueryParam;
import model.QueryParamsList;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rtextarea.RTextScrollPane;

import sparql.SparqlQueryExecutor;
import sparql.SparqlQueryFromString;
import view.EndPointPane;

public class QueryDebugFrame extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	private RSyntaxTextArea queryPanel;
	private JPanel contentPane;
	private EndPointPane parent;
	private JButton refreshButton;
	private JPanel bottomPane;
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
		//queryPanel.setPreferredSize(new Dimension(400,400));
		RTextScrollPane queryPanelScroll = new RTextScrollPane(queryPanel);
		
		queryPanelScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		queryPanelScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		contentPane.add(queryPanelScroll, BorderLayout.CENTER);
		contentPane.setPreferredSize(new Dimension(400,400));
		
		bottomPane = new JPanel();
		refreshButton = new JButton("refresh");
		refreshButton.addActionListener(this);
		bottomPane.add(refreshButton);
		contentPane.add(bottomPane, BorderLayout.SOUTH);
		
		init();
		pack();
	}
	@SuppressWarnings("unchecked")
	public void init(){
		String query = parent.getEditor().getQuery();
		QueryParamsList list = parent.getEditor().getParamList();
		EndPointConfig config = EndPointStore.get().getSelectedConfig();
		int charMax = config.getCharMax();
		SparqlQueryExecutor executor = new SparqlQueryExecutor(config, new SparqlQueryFromString(query), null);
		if(list != null){
			for(AbstractQueryParam param : list.getParams()){
				if(param.getType() == AtomicQueryParam.TYPE){
					executor.setValue(param.getName(), param.renderValue());
				}
				if(param.getType() == ListedQueryParam.TYPE){
					System.out.println("list");
					executor.expandList(param.getName(), (List<String>)param.getValue(), SparqlQueryExecutor.FLAG_USING_NAMESPACE);
				}
			}
		}
		Map<String, Integer> queries = executor.querySplit();
		String text = "";
		if(queries.size() == 1){
			text = executor.getQueryString();
		}else{//en cas de split
			text +="Query has been splitted into "+queries.size()+" sub-queries\n\n";
			int part = 0;
			for(String str : queries.keySet()){
				text += "\n\nsub query "+(++part)+"/"+queries.size()+" ("+str.length()+"/"+charMax+" characters )\n";
				text += str;
			}
		}
		queryPanel.setText(text);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		init();
		
	}

	
}
