import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import model.EndPointLocation;

public class EndPointFrame extends JFrame implements ActionListener, Observer{
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTabbedPane tabbedPane;
	
	//menu
	private JMenuBar menuBar;
	private JMenu menuEndPoint;
	private JMenuItem itemSelectEndPoint;
	private JMenuItem itemAddTab;
	private JMenuItem itemDelTab;
	
	private EndPointLocation endPointLocation;
	private List<EndPointPane> panes;
	public EndPointFrame() {
		super("EndPoint Client");
		
		endPointLocation = new EndPointLocation();
		
		menuBar = new JMenuBar();
		menuEndPoint = new JMenu("EndPoint");
		
		itemSelectEndPoint = new JMenuItem("Select EndPoint location");
		itemSelectEndPoint.addActionListener(this);
		menuEndPoint.add(itemSelectEndPoint);
		
		itemAddTab = new JMenuItem("Add Tab...");
		itemDelTab = new JMenuItem("Del Tab...");
		itemAddTab.addActionListener(this);
		itemDelTab.addActionListener(this);
		menuEndPoint.add(itemAddTab);
		menuEndPoint.add(itemDelTab);
		
		
		menuBar.add(menuEndPoint);
		setJMenuBar(menuBar);
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());

		tabbedPane = new JTabbedPane();
		panes = new ArrayList<EndPointPane>();
		addPane("tab1");
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		setContentPane(contentPane);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == itemSelectEndPoint){
			SourceSelector selector = new SourceSelector(endPointLocation);
			selector.setVisible(true);
		}
		if(event.getSource() == itemAddTab){
			String s = (String)JOptionPane.showInputDialog(this,
					"Enter the tab name to add..."
					);
			if(!s.isEmpty())
				addPane(s);
			return;
		}
		if(event.getSource() == itemDelTab){
			String s = (String)JOptionPane.showInputDialog(this,
					"Enter the tab name to delete..."
					);
			if(!s.isEmpty())
				removePane(s);
			return;
		}
		
	}
	public EndPointLocation getEndPointLocation(){
		return endPointLocation;
	}
	public void setEndPointLocation(EndPointLocation target){
		endPointLocation = target;
	}

	@Override
	public void update(Observable o, Object arg) {

		
	}
	public EndPointPane addPane(String name){
		if(getPaneByName(name) != null){
			System.out.println("name already used");
			return null;
		}
		EndPointPane pane = new EndPointPane(this, name);
		panes.add(pane);
		tabbedPane.add(pane, name);
		return pane;
	}
	public void removePane(String name){
		EndPointPane pane = getPaneByName(name);
		if(pane != null){
			int index = panes.indexOf(pane);
			panes.remove(index);
			tabbedPane.remove(index);

		}
	}
	public EndPointPane getPaneByName(String name){
		for(EndPointPane pane : panes)
			if(pane.getName().equals(name))
				return pane;
		return null;
	}
	public void runQueryInNewPane(String query, String name){
		EndPointPane pane = addPane(name);
		if(pane == null)//si on arrive pas a créer le tab on arrete tout
			return;
		pane.setQueryAndRun(query);//on lance la resuete dans le tab
		tabbedPane.setSelectedIndex(panes.size()-1);//et on se met dessus
		
	}
	




}
