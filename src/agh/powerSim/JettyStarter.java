package agh.powerSim;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.mortbay.http.SocketListener;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHttpContext;

public class JettyStarter {

	public static int port = 8080;

	public static String getSimulationsUrl = "getSimulations";

	public static String getSimulationsGetDataUrl = "getSimulationData";

	private Server server;
	
	public static void main(String[] args) {
		JettyStarter server = new JettyStarter();
		server.start();
	}

	public void stop(){
		try {
			server.stop();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void start() {
		try {
			server = new Server();
			SocketListener listener = new SocketListener();

			System.out.println("Max Thread :" + listener.getMaxThreads() + " Min Thread :" + listener.getMinThreads());

			listener.setHost("localhost");
			listener.setPort(port);
			listener.setMinThreads(1);
			listener.setMaxThreads(20);
			server.addListener(listener);

			ServletHttpContext context = (ServletHttpContext) server.getContext("/");
			context.addServlet("/" + getSimulationsUrl, "agh.powerSim.servlet.SimulationsServlet");
			context.addServlet("/" + getSimulationsGetDataUrl, "agh.powerSim.servlet.SimulationGetDataServlet");

			server.start();
			server.join();

			/*
			 * //We will create our server running at http://localhost:8070
			 * Server server = new Server(); server.addListener(":8070");
			 * 
			 * //We will deploy our servlet to the server at the path '/' //it
			 * will be available at http://localhost:8070 ServletHttpContext
			 * context = (ServletHttpContext) server.getContext("/");
			 * context.addServlet("/MO", "jetty.HelloWorldServlet");
			 * 
			 * server.start();
			 */

		} catch (Exception ex) {
			Logger.getLogger(JettyStarter.class.getName()).log(Level.SEVERE, null, ex);
		}

	}
}
