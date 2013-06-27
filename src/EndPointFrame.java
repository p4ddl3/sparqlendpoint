import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import java.awt.* ;

public class EndPointFrame extends JFrame implements ActionListener, Observer{
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	//file selection
	private JPanel fileSelecPanel;
	private JTextField fileNameField;
	private JButton selectRDFsource;
	
	//query execution
	private JPanel queryPanel;
	private JTabbedPane tabbedQueryPane;
	private JEditorPane queryPane;
	private JEditorPane errorPane;
	private JButton queryExecuteButton;
	
	//result view
	private JTable resultView;
	private ResultViewModel viewModel;
	
	private ModelTarget target;
	

	
	
	public EndPointFrame() {
		super("EndPoint");
		this.target = new ModelTarget();
		this.target.addObs(this);
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		setContentPane(contentPane);
		
		fileSelecPanel = new JPanel();
		fileSelecPanel.setLayout(new BorderLayout());
		
		fileNameField = new JTextField();
		fileNameField.setEditable(false);
		fileSelecPanel.add(fileNameField, BorderLayout.CENTER);
		
		selectRDFsource = new JButton("Select RDF");
		selectRDFsource.addActionListener(this);
		fileSelecPanel.add(selectRDFsource, BorderLayout.EAST);
		
		contentPane.add(fileSelecPanel, BorderLayout.NORTH);
		
		
		queryPanel = new JPanel();
		queryPanel.setLayout(new BorderLayout());
		
		tabbedQueryPane = new JTabbedPane();
		
		
		tabbedQueryPane.add(new LineNumberingTextArea((queryPane = new JEditorPane())), "Query");

		
		errorPane = new JEditorPane();
		errorPane.setEditable(false);
		errorPane.setForeground(Color.red);
		tabbedQueryPane.add(new LineNumberingTextArea(errorPane), "Errors");
		
		queryPanel.add(tabbedQueryPane, BorderLayout.CENTER);
		
		
		queryExecuteButton = new JButton("Execute");
		queryExecuteButton.addActionListener(this);
		queryExecuteButton.setEnabled(false);
		queryPanel.add(queryExecuteButton, BorderLayout.EAST);
		queryPanel.setPreferredSize(new Dimension(200, 200));
		//contentPane.add(queryPanel, BorderLayout.SOUTH);
		
		
		viewModel = new ResultViewModel();
		viewModel.setModel(target);
		resultView = new JTable(viewModel);
		JScrollPane scroll = new JScrollPane(resultView);
		//contentPane.add(scroll, BorderLayout.CENTER);
		
		//splitPane
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.add(scroll);
		splitPane.add(queryPanel);
		contentPane.add(splitPane, BorderLayout.CENTER);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
	}


	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == queryExecuteButton){
			String query = queryPane.getText();
			target.executeQuery(query);
		}
		if(event.getSource() == selectRDFsource){
			SourceSelector selector = new SourceSelector(target);
			selector.setVisible(true);
		}
		
	}


	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg0 instanceof ModelTarget){
			ModelTarget targ = (ModelTarget) arg0;
			if(targ.isReady())
				queryExecuteButton.setEnabled(true);
			else
				queryExecuteButton.setEnabled(false);
			fileNameField.setText(targ.getURL());
			errorPane.setText(targ.getErrorMessage());
			if(!errorPane.getText().equals("")){//il y'a une erreur
				tabbedQueryPane.setSelectedIndex(1);
			}
		}
		setTitle("EndPoint ("+viewModel.getNbLines()+" lines selected)");
		
	}

}
