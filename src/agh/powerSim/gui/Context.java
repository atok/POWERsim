package agh.powerSim.gui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;

import javax.swing.text.html.ObjectView;

import org.w3c.dom.views.AbstractView;

import agh.powerSim.Starter;
import agh.powerSim.simulation.Simulation;
import agh.powerSim.simulation.SimulationLoader;

/**
 * klasa udostępniająca interfejs do przejść między widokami
 * 
 * @author Darek
 * 
 */
public class Context {

	private static final String BASE_FXML_PATH = "/fxml/";

	private static final String FXML_SUFFIX = ".fxml";

	private static Logger log = Logger.getLogger(Window.class.getName());
	
	private static Controller controller;


	@SuppressWarnings({ "rawtypes" })
	public static Controller loadView(String viewPath) {

		try {
			controller = loadFxml(viewPath);
			return controller;
		} catch (IOException e) {
			log.log(Level.WARNING, "Error while loading " + viewPath
					+ ".fxml occuder; " + e.getMessage());
			return null;
		}
	}

	public static void launch() {
		Application.launch(Window.class, new String[] {});
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Controller loadFxml(String viewPath) throws IOException {
		InputStream fxmlStream = Context.class
				.getResourceAsStream(BASE_FXML_PATH + viewPath + FXML_SUFFIX);
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Context.class.getResource(BASE_FXML_PATH));
		Parent root = (Parent) loader.load(fxmlStream);
		Controller controller = (Controller) loader.getController();
		return controller;
	}

	public static Parent getView() {
		return controller.getRoot();
	}

	public static int duration = 1;
	
	public static int delay = 1000;
	
	public static File file;
	
	private static ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(1);
	
	public static void runSimulation(File file, int duration, int delay) {

		Context.duration=duration;
		Context.delay=delay;
		Context.file=file;
		
		threadPoolExecutor.execute(new Runnable() {
			
			@Override
			public void run() {
				Starter.main(null);
				
			}
		});
		
	}
	
	public static void setWeather(final String sun,final String temp,final String clouds){
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				controller.weatherIsSun.setText(sun);
				controller.weatherClouds.setText(clouds);
				controller.weatherTemp.setText(temp);
				
			}
		});
	}
	
	public static void setTime(final String date){
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				controller.simulationDate.setText(date);
			}
		});
	}

	public static void enable() {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				controller.delay.setDisable(false);
				controller.duration.setDisable(false);
				controller.startButton.setText("Start");
				controller.startButton.setDisable(false);
				controller.openButton.setDisable(false);
				
			}
		});
		
	}
}
