package view;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

public class EndPointConfigCreationDialog extends JDialog implements ActionListener{
	private static final long serialVersionUID = 1L;

	private JPanel 			contentPane;
	private JPanel 			panelSelection;
	private JPanel 			bottomPane;
	private JTextField 		localSourceText;
	private JTextField 		remoteSourceText;
	private JTextField 		nameSourceText;
	private JButton 		okButton;
	private JButton 		browseButton;
	private JButton 		paramsButton;
	private JFileChooser 	fileChooser;
	private JRadioButton 	local;
	private JRadioButton 	remote;
	
	public EndPointConfigCreationDialog(JFrame frame){
		super(frame, "Create a new endpoint configuration",true);
		
		contentPane = new JPanel();
		setContentPane(contentPane);
		
		fileChooser = new JFileChooser();
		

		localSourceText = new JTextField();
		browseButton = new JButton("browse");
		browseButton.addActionListener(this);
		
		paramsButton = new JButton("params");
		paramsButton.addActionListener(this);
		remoteSourceText = new JTextField();
		okButton = new JButton("Create");
		okButton.addActionListener(this);
		
		nameSourceText = new JTextField();
		nameSourceText.setColumns(20);
		
		contentPane.setLayout(new BorderLayout(10,10));
		
		panelSelection = new JPanel();
		panelSelection.setLayout(new GridBagLayout());
		panelSelection.setBorder(BorderFactory.createTitledBorder("Create a new endpoint configuration"));
		
		
		local = new JRadioButton("local file");
		local.setSelected(true);
		local.addActionListener(this);
		remote = new JRadioButton("remote endpoint (type url)");
		remote.addActionListener(this);
		
		ButtonGroup group = new ButtonGroup();
		group.add(local);
		group.add(remote);
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.0;
		c.gridwidth = 3;
		c.gridwidth = 1;
		panelSelection.add(new JLabel("Endpoint name :"));
		c.gridx = 1;
		panelSelection.add(nameSourceText,c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		
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
		
		contentPane.add(panelSelection, BorderLayout.CENTER);
		
		bottomPane = new JPanel();
		bottomPane.add(okButton);
		contentPane.add(bottomPane, BorderLayout.SOUTH);
		setResizable(false);
		pack();
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
