package view.results;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import view.EndPointPane;


public class CellTablePopUp extends JPopupMenu implements ActionListener{
	private static final long serialVersionUID = 1L;
	private JMenuItem copyItem;
	private JMenuItem queryParentAndAttributeItem;
	private JMenuItem queryParentItem;
	private JMenuItem queryAttributeItem;
	private String object;
	private EndPointPane pane;
	public CellTablePopUp(EndPointPane pane, String object){
		this.pane = pane;
		this.object = object;
		copyItem = new JMenuItem("copy to clipboard");
		copyItem.addActionListener(this);
		copyItem.setActionCommand("results.popup.copy");
		queryParentAndAttributeItem = new JMenuItem("get attributes and parents");
		queryParentAndAttributeItem.setActionCommand("results.popup.query.parent&attribute");
		queryParentAndAttributeItem.addActionListener(this);
		queryParentItem = new JMenuItem("get parents");
		queryParentItem.setActionCommand("results.popup.query.parent");
		queryAttributeItem = new JMenuItem("get attributes");
		queryAttributeItem.addActionListener(this);
		queryAttributeItem.setActionCommand("results.popup.query.attribute");
		queryParentItem.addActionListener(this);
		JMenuItem title = new JMenuItem(object);
		title.setEnabled(false);
		add(title);
		addSeparator();
		add(copyItem);
		add(queryParentItem);
		add(queryAttributeItem);
		add(queryParentAndAttributeItem);
	}
	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getActionCommand().equals("results.popup.copy")){
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Clipboard clipboard =  toolkit.getSystemClipboard();
			StringSelection strselec = new StringSelection(this.object);
			clipboard.setContents(strselec, null);
			return;
		}
		if(event.getActionCommand().equals("results.popup.query.parent&attribute")){
			String query = 	"select ?isValueOf ?predicate ?hasValue \n"+
							"where {{\n"+
							"\t?isValueOf ?predicate "+getEnclosedObject(object)+".\n"+
							"\t}UNION{\n"+
							"\t<"+object+"> ?predicate ?hasValue\n"+
							"}}";
			pane.getFrame().runQueryInNewPane(query, object);
			return;
		}
		if(event.getActionCommand().equals("results.popup.query.parent")){
			String query = 	"select ?isValueOf ?predicate  \n"+
							"where {\n"+
							"\t?isValueOf ?predicate "+getEnclosedObject(object)+".\n}";
			pane.getFrame().runQueryInNewPane(query, object);
			return;
		}
		
		if(event.getActionCommand().equals("results.popup.query.attribute")){
			String query = 	"select ?predicate  ?hasValue\n"+
							"where {\n"+
							"\t"+getEnclosedObject(object)+" ?predicate ?hasValue.\n}";
			pane.getFrame().runQueryInNewPane(query, object);
			return;
		}
		
	}
	public String getEnclosedObject(String obj){
		String str = null;
		if(obj.startsWith("http")){
			str = "<"+obj+">";
		}else
			str = "\""+obj.split("\\^\\^")[0]+"\"";
		return str;
	}
}
