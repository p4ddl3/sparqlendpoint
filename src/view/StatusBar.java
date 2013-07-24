package view;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.plaf.ProgressBarUI;

public class StatusBar extends JPanel{
	private String message;
	private static final long serialVersionUID = 1L;
	private Container parent;
	private JLabel statusLabel;
	private Color messageColor;
	private JProgressBar progressBar;
	private static StatusBar instance;
	private Timer timer;
	public static StatusBar get(){
		if(instance == null)
			instance = new StatusBar();
		return instance;
	}
	public void attachTo(Container parent){
		this.parent = parent;
		init();
	}
	private StatusBar(){
		super();
		timer = new Timer();
	}
	public void setMessage(String message){
		this.message = message;
		statusLabel.setForeground(messageColor);
		statusLabel.setText(message);
		
	}
	public void setColor(Color color){
		this.messageColor = color;
	}
	public Color getColor(){
		return messageColor;
	}
	public void setMessage(String text, Color color){
		setColor(color);
		setMessage(text);
		setColor(Color.black);
	}
	public String getMessage(){
		return message;
	}
	private void init(){
		setBorder(new BevelBorder(BevelBorder.LOWERED));
		setPreferredSize(new Dimension(parent.getWidth(), 20));

		statusLabel = new JLabel();
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		
		progressBar = new JProgressBar();
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridheight = 2;
		c.weightx =1;
		c.anchor = GridBagConstraints.WEST;
		add(statusLabel,c);
		c.weightx =0;
		c.gridx = 1;
		c.anchor = GridBagConstraints.EAST;
		add(progressBar,c);
		
		
		parent.add(this, BorderLayout.SOUTH);
		messageColor = Color.black;
	}
	public void out(String message){
		timer.cancel();
		messageColor = Color.black;
		setMessage(message, messageColor);
	}
	public void out(String message, long time){
		timer.cancel();
		messageColor = Color.black;
		setMessage(message, messageColor);
		timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				setMessage("", messageColor);
				
			}
		}, time);
	}
	public void err(String message){
		timer.cancel();
		messageColor = Color.red;
		setMessage(message, messageColor);
	}
	public void err(String message, long time){
		timer.cancel();
		messageColor = Color.red;
		setMessage(message, messageColor);
		timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				setMessage("", messageColor);
				
			}
		}, time);
	}
	public void publishProgress(int progress){
		if(progress <= 0 || progress >= 100){
			progressBar.setVisible(false);
			return;
		}
		else{
			progressBar.setVisible(true);
			progressBar.setValue(progress);
		}
		
	}


}
