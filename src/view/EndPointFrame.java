package view;
import java.awt.BorderLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.openjena.atlas.iterator.ActionCount;

import model.EndPointConfig;
import model.EndPointStore;
import view.results.TabPopup;

public class EndPointFrame extends JFrame implements ActionListener, Observer, MouseListener{
	private static final long serialVersionUID = 1L;
	
	private static Map<KeyStroke,Action> actionMap;
	
	private JPanel contentPane;
	private JTabbedPane tabbedPane;
	
	//menu
	private JMenuBar menuBar;
	private JMenu menuEndPoint;
	private JMenuItem itemSelectEndPoint;
	private JMenuItem itemAddTab;
	private JMenuItem itemDelTab;
	
	private EndPointConfig endPointLocation;
	private List<EndPointPane> panes;
	public EndPointFrame() {
		super("EndPoint Client");
		
		EndPointStore.get();
		endPointLocation = new EndPointConfig();
		
		menuBar = new JMenuBar();
		menuEndPoint = new JMenu("EndPoint");
		
		itemSelectEndPoint = new JMenuItem("Configure endpoint");
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
		tabbedPane.addMouseListener(this);
		panes = new ArrayList<EndPointPane>();
		addPane("tab1");
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		setContentPane(contentPane);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		EndPointStore.get().addObserver(this);
		setup();
		StatusBar.get().attachTo(this);
		StatusBar.get().out("Ready.");
		pack();
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == itemSelectEndPoint){
			EndpointConfigurationFrame selector = new EndpointConfigurationFrame(this);
			selector.setVisible(true);
		}
		if(event.getSource() == itemAddTab){
			createPane();
		}
		if(event.getSource() == itemDelTab){
			if(panes.size() == 0)
				return;
			String[] choices = new String[panes.size()];
			for(int i = 0; i < panes.size(); i++){
				choices[i] = panes.get(i).getName();
			}
			String s = (String)JOptionPane.showInputDialog(
                    this,
                    "Select a tab to delete :",
                    "delete a tab",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    choices,
                    choices[0]);

			if(s!= null && !s.isEmpty())
				removePane(s);
			return;
		}
		
	}
	public void createPane(){
		String s = (String)JOptionPane.showInputDialog(this,
				"Enter the tab name to add...",
				"tab"+(panes.size()+1)
				);
		if(s!= null && !s.isEmpty())
			addPane(s);
		return;
	}
	public EndPointConfig getEndPointLocation(){
		return endPointLocation;
	}
	public void setEndPointLocation(EndPointConfig target){
		endPointLocation = target;
	}

	@Override
	public void update(Observable o, Object action) {
		if(action == null)
			return;
		if(action.equals("endpointstore.update.selection")){
			setTitle(EndPointStore.get().getSelectedName()+" Client");
			return;
		}
			
		
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

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3){
			EndPointPane pane = (EndPointPane)tabbedPane.getSelectedComponent();
			TabPopup popup = new TabPopup(this,pane.getName());
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}
	
	public  void setup(){
		actionMap = new HashMap<KeyStroke, Action>();
		KeyStroke ctrl_F = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK);
		KeyStroke ctrl_N = KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK);
		KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		actionMap.put(ctrl_F, new AbstractAction("search"){
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				int index = tabbedPane.getSelectedIndex();
				EndPointPane pane = panes.get(index);
				pane.showPanel(true);
				
			}
			
		});
		actionMap.put(ctrl_N, new AbstractAction("main.pane.add"){
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				createPane();
				
			}
			
		});
		actionMap.put(escape, new AbstractAction("main.search.dismiss") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				int index = tabbedPane.getSelectedIndex();
				EndPointPane pane = panes.get(index);
				pane.showPanel(false);
				
			}
		});
		KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		kfm.addKeyEventDispatcher(new KeyEventDispatcher(){

			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);
				if(actionMap.containsKey(keyStroke)){
					final Action a = actionMap.get(keyStroke);
					final ActionEvent ae = new ActionEvent(e.getSource(),e.getID(),null);
					SwingUtilities.invokeLater(new Runnable(){

						@Override
						public void run() {
							a.actionPerformed(ae);
							
						}
						
					});
					return true;
				}
				return false;
			}
			
		});
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
	




}
