import org.apache.log4j.BasicConfigurator;


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
