package view;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import model.EndPointConfig;
import model.EndPointStore;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;


public class EndpointConfigurationFrame extends JDialog implements ItemListener, ActionListener{
	private static final long serialVersionUID = 1L;
	
	
	private JPanel contentPane;
	private JPanel selectionPanel;
	private JComboBox<String> existingComboBox;
	
	//config preview
	private JPanel previewPanel;
	private JTextField nameField;
	private JTextField urlField;
	private JCheckBox remoteCheckBox;
	private RSyntaxTextArea paramsArea;
	
	
	//bottom
	private JPanel bottomPane;
	private JButton applyButton;
	private JButton editButton;
	private JButton saveButton;
	private JButton removeButton;
	
	private boolean editMode;
	private String oldName;
	private String errorMessage;
	
	private Map<String, String> paramsNeeded;
	public EndpointConfigurationFrame(EndPointFrame frame) {
		super(frame, "Endpoint configuration", true);
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		setContentPane(contentPane);
				
		selectionPanel = new JPanel();
		selectionPanel.setBorder(BorderFactory.createTitledBorder("Select an existing configuration"));
		selectionPanel.setPreferredSize(new Dimension(500,200));
		selectionPanel.setLayout(new BorderLayout());
		existingComboBox = new JComboBox<String>();
		existingComboBox.addItemListener(this);
		JPanel p = new JPanel();
		p.add(existingComboBox);
		selectionPanel.add(p, BorderLayout.NORTH);
		
		previewPanel = new JPanel();
		previewPanel.setLayout(new GridBagLayout());
		
		nameField = new JTextField();
		
		urlField = new JTextField();
		urlField.setColumns(15);
		
		remoteCheckBox = new JCheckBox("(remote) URL : ");
		
		
		paramsArea = new RSyntaxTextArea();
		paramsArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE);
		RTextScrollPane scroll = new RTextScrollPane(paramsArea);
		scroll.setPreferredSize(new Dimension(150,200));
		
		
		bottomPane = new JPanel();
		
		applyButton = new JButton("apply");
		applyButton.setActionCommand("endpoint.configure.select");
		applyButton.addActionListener(this);
		bottomPane.add(applyButton);
		
		editButton = new JButton("edit");
		editButton.setActionCommand("endpoint.configure.edit");
		editButton.addActionListener(this);
		bottomPane.add(editButton);
		
		saveButton = new JButton("save");
		saveButton .setActionCommand("endpoint.configure.save");
		saveButton.addActionListener(this);
		bottomPane.add(saveButton);
		
		removeButton = new JButton("remove");
		removeButton.setActionCommand("endpoint.configure.remove");
		removeButton.addActionListener(this);
		bottomPane.add(removeButton);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		previewPanel.add(new JLabel("name : "), c);
		c.gridx = 2;
		previewPanel.add(nameField,c);
		c.gridy = 1;
		c.gridx = 0;
		previewPanel.add(remoteCheckBox, c);
		c.gridx = 2;
		previewPanel.add(urlField, c);
		c.gridy = 3;
		c.gridx = 0;

		c.gridy = 3;
		c.weightx=1;
		c.weighty = 1;
		c.ipady = 50;
		c.gridx=0;
		previewPanel.add(new JLabel("params :"), c);
		c.gridx = 2;
		previewPanel.add(scroll, c);
		
		previewPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		selectionPanel.add(previewPanel, BorderLayout.CENTER);
		contentPane.add(selectionPanel, BorderLayout.CENTER);
		contentPane.add(bottomPane, BorderLayout.SOUTH);
		pack();
		refreshComboBox();
		init();
		refresh();

	}

	public void refreshComboBox(){
		existingComboBox.removeAllItems();
		for(String str : EndPointStore.get().getNamesArray()){
			existingComboBox.addItem(str);
		}
		existingComboBox.setSelectedItem(EndPointStore.get().getSelectedName());
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		
		refresh();
	}
	public void refresh(){
		
		
		String name = (String)existingComboBox.getSelectedItem();
		EndPointConfig config = EndPointStore.get().getConfig(name);
		if(config != null){
			nameField.setText(config.getName());
			urlField.setText(config.getURL());
			remoteCheckBox.setSelected(config.isRemote());
			Map<String, String> params = config.getParams();
			String text = "";
			for(Entry< String, String> entry : params.entrySet())
				text += entry.getKey()+"="+entry.getValue()+"\n";
			paramsArea.setText(text);
		}else{
			nameField.setText("");
			urlField.setText("");
			remoteCheckBox.setSelected(false);
			paramsArea.setText("");
		}
		if(editMode){
			editButton.setEnabled(false);
			applyButton.setEnabled(false);
			saveButton.setEnabled(true);
			existingComboBox.setEnabled(false);
			removeButton.setEnabled(true);
			
			nameField.setEnabled(true);
			urlField.setEnabled(true);
			remoteCheckBox.setEnabled(true);
			paramsArea.setEnabled(true);
		}else{
			editButton.setEnabled(true);
			applyButton.setEnabled(true);
			saveButton.setEnabled(false);
			existingComboBox.setEnabled(true);
			removeButton.setEnabled(false);
			
			nameField.setEnabled(false);
			urlField.setEnabled(false);
			remoteCheckBox.setEnabled(false);
			paramsArea.setEnabled(false);
		}
		
	}
	public void init(){
		String name = EndPointStore.get().getSelectedName();
		if(name != null)
			existingComboBox.setSelectedItem(name);
		editMode = false;
		paramsNeeded = new HashMap<String, String>();
		paramsNeeded.put("charmax", "[0-9]+");
		errorMessage = "";
	}


	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getActionCommand().equals("endpoint.configure.select")){
			EndPointStore.get().setSelectedName((String) existingComboBox.getSelectedItem());
			setVisible(false);
			return;
		}
		if(event.getActionCommand().equals("endpoint.configure.edit")){
			editMode = true;
			oldName = (String)existingComboBox.getSelectedItem();
			refresh();
			return;
		}
		if(event.getActionCommand().equals("endpoint.configure.save")){
			if(save()){
				editMode = false;
				refresh();
			}
			return;
		}
		if(event.getActionCommand().equals("endpoint.configure.remove")){
			EndPointStore.get().remove((String) existingComboBox.getSelectedItem());
			editMode = false;
			refreshComboBox();
			refresh();
			return;
		}
		
	}
	public boolean save(){
		boolean success = true;
		
		//test du nom
		String newName = nameField.getText();
		if(EndPointStore.get().containsExceptOther(newName, oldName)){
			success= false;
			errorMessage = "This name is already taken";
		}
		
		//parametres
		if(success)
			success= checkParams();
		
		if(!success){
			JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
		}
		if(success){
			write();
		}
		return success;
	}
	public boolean checkParams(){
		boolean success = true;
		Pattern paramPattern = Pattern.compile("(?<key>.+)=(?<value>.+)");
		String text = paramsArea.getText();
		if(text.equals("")){
			if (!paramsNeeded.isEmpty()){
				errorMessage = paramsNeeded.keySet() +" parameter(s) needed";
				return false;
			}
		}
		String[] lines = text.split("\n");
		List<String> list = new ArrayList<String>();
		for(String line : lines){
			if(line.isEmpty())
				continue;
			Matcher match = paramPattern.matcher(line);
			if(!match.matches()){
				errorMessage = line + "\nInvalid parameter";
				return false;
			}else{
				String key = match.group("key");
				if(list.contains(key)){
					errorMessage = key +"\nAlready used parameter";
					return false;
				}else
					list.add(key);
			}
		}
		for(String key : paramsNeeded.keySet()){
			String value = paramsNeeded.get(key);
			Pattern pattern = Pattern.compile("(?<key>"+key+")=(?<value>"+value+")");
			boolean found = false;
			for(String line : lines){
				Matcher matcher = pattern.matcher(line);
				if(matcher.matches()){
					found = true;
					break;
				}
			}
			if(!found){
				errorMessage = key+" parameter is needed but not found or malformed.\nIt's must be formed as : "+value;
				return false;
			}
		}
		return success;
	}
	public void write(){
		EndPointConfig config = new EndPointConfig();
		config.setName(nameField.getText());
		config.setURL(urlField.getText());
		config.setRemote(remoteCheckBox.isSelected());
		Pattern paramPattern = Pattern.compile("(?<key>.+)=(?<value>.+)");
		for(String str : paramsArea.getText().split("\n")){
			Matcher matcher = paramPattern.matcher(str);
			matcher.matches();
			String k, v;
			k = matcher.group("key");
			v = matcher.group("value");
			config.setParams(k, v);
		}
		EndPointStore.get().put(config);
		refreshComboBox();
		refresh();
	}

}
