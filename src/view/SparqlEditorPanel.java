package view;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rtextarea.RTextScrollPane;

public class SparqlEditorPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private RSyntaxTextArea queryPane;
	private JPanel bottomPane;
	private EndPointPane parent;
	private JButton executeButton;
	private JButton showQueryButton;
	
	public SparqlEditorPanel(EndPointPane parent){
		setLayout(new BorderLayout());
		this.parent = parent;
		queryPane = new RSyntaxTextArea();
		AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory)TokenMakerFactory.getDefaultInstance();
		atmf.putMapping("text/sparql", "syntax.SparqlTokenMaker");
		queryPane.setSyntaxEditingStyle("text/sparql");
		queryPane.setAntiAliasingEnabled(true);
		queryPane.setMarkOccurrences(true);
		queryPane.setMarkOccurrencesColor(Color.pink);
		RTextScrollPane scrollp = new RTextScrollPane(queryPane);
		add(scrollp, BorderLayout.CENTER);
		
		bottomPane = new JPanel();
		executeButton = new JButton("execute");
		executeButton.setActionCommand("editor.query.execute");
		executeButton.addActionListener(parent);
		
		showQueryButton = new JButton("show query with params");
		showQueryButton.setActionCommand("editor.query.show");
		showQueryButton.addActionListener(parent);
		bottomPane.add(executeButton);
		bottomPane.add(showQueryButton);
		add(bottomPane, BorderLayout.SOUTH);
		refresh();
	}
	public void setText(String text){
		queryPane.setText(text);
	}
	public String getText(){
		return queryPane.getText();
	}
	public void refresh(){
		if(parent.getEndPointLocation().isLoaded()){
			executeButton.setEnabled(true);
		}else
			executeButton.setEnabled(false);
	}

}
