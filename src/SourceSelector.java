import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import model.EndPointLocation;


public class SourceSelector extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	private JLabel actualLocation;
	private JPanel contentPane;
	private JPanel panelSelection;
	private JPanel bottomPane;
	private JTextField localSourceText;
	private JTextField remoteSourceText;
	private JButton okButton;
	private JButton browseButton;
	private JButton paramsButton;
	private JFileChooser fileChooser;
	private JRadioButton local;
	private JRadioButton remote;
	
	
	private EndPointLocation location;
	
	public SourceSelector(EndPointLocation location) {
		super("RDF source selection");
		contentPane = new JPanel();
		setContentPane(contentPane);
		this.location = location;
		fileChooser = new JFileChooser();
		actualLocation = new JLabel();
		if(location.getURL()!= null){
			actualLocation.setText("The current endpoint localization is :"+location.getURL()+
					" ("+(location.isRemote()?"remote":"local")+")");
		}else{
			actualLocation.setText("You haven't selected an endpoint localization yet. Please select one.");
		}
		
		localSourceText = new JTextField();
		localSourceText.setColumns(20);
		browseButton = new JButton("browse");
		browseButton.addActionListener(this);
		
		paramsButton = new JButton("params");
		paramsButton.addActionListener(this);
		remoteSourceText = new JTextField();
		okButton = new JButton("Apply");
		okButton.addActionListener(this);
		contentPane.setLayout(new BorderLayout(10,10));
		
		panelSelection = new JPanel();
		panelSelection.setLayout(new GridBagLayout());
		panelSelection.setBorder(BorderFactory.createTitledBorder("Endpoint localization"));
		
		
		local = new JRadioButton("local file");
		local.setSelected(true);
		local.addActionListener(this);
		remote = new JRadioButton("remote endpoint (type url)");
		remote.addActionListener(this);
		
		ButtonGroup group = new ButtonGroup();
		group.add(local);
		group.add(remote);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.0;
		c.gridwidth = 3;
		
		c.gridy = 1;
		c.gridwidth = 1;
		panelSelection.add(local, c);
		c.gridx = 1;
		panelSelection.add(localSourceText, c);
		c.gridx = 2;
		panelSelection.add(browseButton, c);
		c.gridy = 2;
		c.gridx = 0;
		panelSelection.add(remote, c);
		c.gridx = 1;
		panelSelection.add(remoteSourceText, c);
		c.gridx = 2;
		panelSelection.add(paramsButton, c);
		contentPane.add(actualLocation,BorderLayout.NORTH);
		contentPane.add(panelSelection, BorderLayout.CENTER);
		
		bottomPane = new JPanel();
		bottomPane.add(okButton);
		contentPane.add(bottomPane, BorderLayout.SOUTH);
		setResizable(false);
		init();
		pack();
	}
	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == okButton){
			if(remote.isSelected()){
				location.setRemote(true);
				location.setURL(remoteSourceText.getText());
			}else{
				location.setRemote(false);
				location.setURL(localSourceText.getText());
			}
			location.load();
			setVisible(false);
			return;
		}
		if(event.getSource() == local || event.getSource() == remote){
			refresh();
			return;
		}
		if(event.getSource() == browseButton){
			int returnVal = fileChooser.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION){
				File file = fileChooser.getSelectedFile();
				localSourceText.setText(file.getAbsolutePath());
			}
		}
		if(event.getSource() == paramsButton){
			new ParamsFrame(location);
		}
		
	}
	public void init(){
		if(location.isRemote()){
			remote.setSelected(true);
			remoteSourceText.setText(location.getURL());
		}else{
			local.setSelected(true);
			localSourceText.setText(location.getURL());
		}
		refresh();
	}
	public void refresh(){
		if(remote.isSelected()){
			remoteSourceText.setEnabled(true);
			localSourceText.setEditable(false);
			browseButton.setEnabled(false);
			paramsButton.setEnabled(true);
		}else{
			remoteSourceText.setEnabled(false);
			localSourceText.setEditable(true);
			browseButton.setEnabled(true);
			paramsButton.setEnabled(false);
		}
	}

}
