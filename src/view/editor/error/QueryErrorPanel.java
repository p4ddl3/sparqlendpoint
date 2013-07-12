package view.editor.error;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import model.ResultModel;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import util.Bundle;

public class QueryErrorPanel extends JPanel implements Observer{
	private static final long serialVersionUID = 1L;
	private ResultModel model;
	private RSyntaxTextArea errorPane;
	public QueryErrorPanel(ResultModel model){
		this.model = model;
		model.addObserver(this);
		setLayout(new BorderLayout());
		errorPane = new RSyntaxTextArea();
		errorPane.setForeground(Color.red);
		errorPane.setEditable(false);
		add(new RTextScrollPane(errorPane), BorderLayout.CENTER);
	}
	@Override
	public void update(Observable o, Object arg) {
		String text = model.getErrorMessage();
		if(text == null)
			text = "";
			errorPane.setText(text);
	}
	public void setText(String error){
		errorPane.setText(error);
	}
}
