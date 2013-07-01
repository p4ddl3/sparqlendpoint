import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


public class CellTablePopUp extends JPopupMenu implements ActionListener{
	private static final long serialVersionUID = 1L;
	private JMenuItem copyItem;
	private JMenuItem queryItem;
	private String object;
	private EndPointPane pane;
	public CellTablePopUp(EndPointPane pane, String object){
		this.pane = pane;
		this.object = object;
		copyItem = new JMenuItem("copy");
		copyItem.addActionListener(this);
		queryItem = new JMenuItem("get attributes and parents");
		queryItem.addActionListener(this);
		JMenuItem title = new JMenuItem(object);
		title.setEnabled(false);
		add(title);
		add(copyItem);
		add(queryItem);
	}
	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == copyItem){
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Clipboard clipboard =  toolkit.getSystemClipboard();
			StringSelection strselec = new StringSelection(this.object);
			clipboard.setContents(strselec, null);
			return;
		}
		if(event.getSource() == queryItem){
			String query = 	"select ?isValueOf ?p ?hasValue \n"+
							"where {{\n"+
							"\t?isValueOf ?p <"+object+">.\n"+
							"\t}UNION{\n"+
							"\t<"+object+"> ?p ?hasValue\n"+
							"}}";
			pane.getFrame().runQueryInNewPane(query, object);
		}
		
	}
}
