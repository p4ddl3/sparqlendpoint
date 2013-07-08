import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.apache.log4j.BasicConfigurator;

import view.EndPointFrame;


public class EndPoint {

	public EndPoint() {
		// TODO Auto-generated constructor stub
	}
	public static void main(String[] args){
		BasicConfigurator.configure();
		EndPointFrame frame = new EndPointFrame();
		frame.setVisible(true);
		//pt
	}

}
