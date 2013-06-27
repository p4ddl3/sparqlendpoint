import java.awt.Color;
 

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;
 
 
public class LineNumberingTextArea extends JScrollPane{
	private  JEditorPane jta;
	private JTextArea lines;
 
	public LineNumberingTextArea(JEditorPane area){
		jta = area;
		lines = new JTextArea("1");
 
		lines.setBackground(Color.LIGHT_GRAY);
		lines.setEditable(false);
 
		jta.getDocument().addDocumentListener(new DocumentListener(){
			public String getText(){
				int caretPosition = jta.getDocument().getLength();
				Element root = jta.getDocument().getDefaultRootElement();
				String text = "1" + System.getProperty("line.separator");
				for(int i = 2; i < root.getElementIndex( caretPosition ) + 2; i++){
					text += i + System.getProperty("line.separator");
				}
				return text;
			}
			@Override
			public void changedUpdate(DocumentEvent de) {
				lines.setText(getText());
			}
 
			@Override
			public void insertUpdate(DocumentEvent de) {
				lines.setText(getText());
			}
 
			@Override
			public void removeUpdate(DocumentEvent de) {
				lines.setText(getText());
			}
 
		});
 
		this.getViewport().add(jta);
		setRowHeaderView(lines);
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	}
	public JEditorPane getTextArea(){
		return jta;
	}
 

}