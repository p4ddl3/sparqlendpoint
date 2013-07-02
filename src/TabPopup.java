import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


public class TabPopup extends JPopupMenu implements ActionListener{
	String name;
	private JMenuItem titleItem;
	private JMenuItem rowItem;
	private JMenuItem deleteItem;
	private EndPointFrame frame;
	public TabPopup(EndPointFrame frame, String tabName){
		super(tabName);
		this.name = tabName;
		this.frame = frame;
		deleteItem = new JMenuItem("delete tab");
		deleteItem.addActionListener(this);
		titleItem = new JMenuItem(tabName);
		titleItem.setEnabled(false);
		ResultViewModel model = frame.getPaneByName(tabName).getViewModel();
		rowItem = new JMenuItem(model.getNbLines()+"/"+model.getNbFullLines()+" rows displayed");
		rowItem.setEnabled(false);
		add(titleItem);
		add(rowItem);
		addSeparator();
		add(deleteItem);
	}
	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == deleteItem){
			frame.removePane(name);
			return;
		}
		
	}
}
