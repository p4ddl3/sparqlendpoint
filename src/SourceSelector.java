import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;


public class SourceSelector extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField sourceText;
	private JButton okButton;
	private JRadioButton local;
	private JRadioButton remote;
	private ModelTarget model;
	public SourceSelector(ModelTarget model) {
		super("RDF source selection");
		contentPane = new JPanel();
		setContentPane(contentPane);
		this.model = model;
		sourceText = new JTextField();
		okButton = new JButton("Select source");
		okButton.addActionListener(this);
		contentPane.setLayout(new GridBagLayout());
		
		
		
		local = new JRadioButton("local file");
		local.setSelected(true);
		remote = new JRadioButton("remote endpoint");
		
		ButtonGroup group = new ButtonGroup();
		group.add(local);
		group.add(remote);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.0;
		c.gridwidth = 3;
		contentPane.add(sourceText, c);
		c.weightx = 0.5;
		c.gridwidth = 1;
		c.gridy = 1;
		contentPane.add(local,c);
		c.gridx = 1;
		contentPane.add(remote, c);
		c.gridx = 2;
		contentPane.add(okButton, c);
		setResizable(false);
		pack();
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(remote.isSelected()){
			model.setRemote(true);
		}else
			model.setRemote(false);
		model.load(sourceText.getText());
		setVisible(false);
		
	}

}
