package view.editor.params;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

public class DialogParamList extends JDialog implements ActionListener{
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPanel bottomPane;
	private JButton applyButton;
	private RSyntaxTextArea area;
	
	private List<String> model;
	public DialogParamList(List<String> model){
		this.model = model;
		setTitle("Param list values");
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout());
		area = new RSyntaxTextArea();
		contentPane.add(new RTextScrollPane(area), BorderLayout.CENTER);
		
		bottomPane = new JPanel();
		applyButton = new JButton("apply");
		applyButton.addActionListener(this);
		bottomPane.add(applyButton);
		contentPane.add(bottomPane, BorderLayout.SOUTH);
		contentPane.setPreferredSize(new Dimension(200,200));
		init();
		pack();
		setModal(true);
	}
	public void init(){
		String text = "";
		for(String line : model){
			text+=line+"\n";
		}
		area.setText(text);
	}
	public void save(){
		model.clear();
		String text = area.getText();
		if(text.equals(""))
			return;
		String[] lines = text.split("\n");
		for(String line : lines){
			if(!line.equals(""))
				model.add(line);
		}
		return;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		save();
		setVisible(false);
	}

}
