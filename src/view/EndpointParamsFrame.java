package view;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import model.EndPointConfig;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;


public class EndpointParamsFrame extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private RSyntaxTextArea paramText;
	private JPanel bottomPane;
	private JButton applyButton;
	
	private EndPointConfig location;
	public EndpointParamsFrame(EndPointConfig location){
		super("Remote endpoint parameter");
		this.location = location;
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout());
		
		paramText = new RSyntaxTextArea();
		paramText.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE);
		paramText.setBorder(BorderFactory.createTitledBorder("Type a parameter a line as 'key=value'"));
		bottomPane = new JPanel();
		applyButton = new JButton("Apply");
		applyButton.addActionListener(this);
		bottomPane.add(applyButton);
		contentPane.add(paramText, BorderLayout.CENTER);
		contentPane.add(bottomPane, BorderLayout.SOUTH);
		init();
		setPreferredSize(new Dimension(400,200));
		pack();
		setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == applyButton){
			if(apply())
				setVisible(false);
			return;
		}
	}
	public void init(){
		String text = "";
		for(String key : location.getParams().keySet()){
			if(!text.equals(""))
				text += "\n";
			text += key+"="+location.getParams().get(key);
		}
		paramText.setText(text);
	}
	public boolean apply(){
		boolean success = true;
		Map<String, String> tmp = null;
		String[] lines = null;
		String errorMessage = "charmax attribute is needed (nb max of caracters per query).\n The value of this parameter needs to be an integer.";
		if(paramText.getText().equals("")){
			location.clearParams();
			success = false;
		}else{
			tmp = new HashMap<String, String>();
			
			Pattern pattern = Pattern.compile("(?<key>.+)=(?<value>.+)");
			Matcher m;
			String text = paramText.getText();
			lines = text.split("\n");
			for(String line : lines){
				m = pattern.matcher(line);
				if(m.matches()){
					String key = m.group("key");
					String value = m.group("value");
					if(tmp.containsKey(key)){
						success = false;
						errorMessage = line+"\nkey '"+key+"' already used !!";
						break;
					}else{
						tmp.put(key, value);
					}
				}
				else{
					errorMessage = line+"\nisn't correct";
					success = false;
					break;
				}
			}
		}
		//check charmax
		if(success){
			Pattern charPattern = Pattern.compile("charmax=(?<value>[0-9]+)");
			Matcher matcher;
			boolean found  = false;
			for(String str : lines ){
				matcher = charPattern.matcher(str);
				if(matcher.matches()){
					found = true;
					break;
				}
			}
			System.out.println(found);
			if(!found){
				success=  false;
				
			}
		}
		if(success){
			location.clearParams();
			for(String k : tmp.keySet()){
				location.setParams(k, tmp.get(k));
			}
		}else{
			JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
		}
		return success;
	}
}
