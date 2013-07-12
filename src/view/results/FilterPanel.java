package view.results;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import view.EndPointPane;

import model.ResultViewModel;


public class FilterPanel extends JPanel implements ActionListener{

		private JLabel filterLabel;
		private JTextField textField;
		private JButton dismissButton;
		private ResultViewModel modelView;
		private EndPointPane parent;
		
	private static final long serialVersionUID = 1L;

	public FilterPanel(EndPointPane parent,ResultViewModel modelView){
		super();
		this.parent = parent;
		this.modelView = modelView;
		filterLabel = new JLabel("Filter :");
		textField = new JTextField();
		textField.setColumns(10);
		textField.addActionListener(this);
		dismissButton = new JButton("dismiss");
		dismissButton.addActionListener(this);
		add(filterLabel);
		add(textField);
		add(dismissButton);
		setVisible(false);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == dismissButton){
			parent.showPanel(false);
			return;
		}
		if(event.getSource() == textField){
			modelView.setFilter(textField.getText());
			return;
		}

	}
	public void activate(){
		setVisible(true);
		textField.requestFocus();
	}
	public void desactivate(){
		setVisible(false);
		modelView.setFilter(null);
	}

}
