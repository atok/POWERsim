package agh.powerSim.gui;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import agh.powerSim.gui.core.AbstractObjectView;
import agh.powerSim.gui.core.AbstractView;
import agh.powerSim.gui.object.ObjectView;

/**
 * klasa udostępniająca interfejs do przejść między widokami
 * 
 * @author Darek
 * 
 */
public class Context {

	private static final String BASE_FXML_PATH = "/fxml/";

	private static final String OBJECT_PATH = "object/object";

	private static final String FXML_SUFFIX = ".fxml";

	private static Logger log = Logger.getLogger(Window.class.getName());

	private static final BorderPane view = new BorderPane();
	
	private static ObservableList<Node> objectContainer;

	@SuppressWarnings("rawtypes")
	private static final Map<String, AbstractView> views = new HashMap<String, AbstractView>();

	@SuppressWarnings("rawtypes")
	public static void loadView(String viewPath) {
		AbstractView page = findView(viewPath);
		if (page != null) {
			page.onViewLoad();
			getView().setCenter(page.getRoot());
		}
	}

	@SuppressWarnings({ "rawtypes" })
	public static AbstractView findView(String viewPath) {

		if (views.containsKey(viewPath)) {
			return views.get(viewPath);
		}
		try {
			AbstractView page = loadFxml(viewPath);
			views.put(viewPath, page);
			return page;
		} catch (IOException e) {
			log.log(Level.WARNING, "Error while loading " + viewPath
					+ ".fxml occuder; " + e.getMessage());
			return null;
		}
	}

	public static void launch() {
		Application.launch(Window.class, new String[] {});
	}

	static BorderPane getView() {
		return view;
	}

	@SuppressWarnings("rawtypes")
	public static AbstractObjectView objectFactory(String type) {
		ObjectView object = null;
		try {
			object = (ObjectView) loadFxml(OBJECT_PATH);
			if(getObjectContainer()!=null){
				getObjectContainer().add(object.getRoot());
			}
			InputStream imageStream = Context.class.getResourceAsStream(BASE_FXML_PATH+"images/"+type+".png");
			if(imageStream==null){
				imageStream = Context.class.getResourceAsStream(BASE_FXML_PATH+"images/"+type+".gif");
			} 
			if(imageStream==null){
				imageStream = Context.class.getResourceAsStream(BASE_FXML_PATH+"images/"+type+".jpg");
			} 
			if(imageStream!=null){
				Image image = new Image(imageStream);
				object.getImage().setImage(image);
			}
			
		} catch (IOException e) {
			log.log(Level.WARNING, "Error while loading object " + type
					+ ".fxml occuder; " + e.getMessage());
		}
		return object;

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static AbstractView loadFxml(String viewPath) throws IOException {
		InputStream fxmlStream = Context.class
				.getResourceAsStream(BASE_FXML_PATH + viewPath + FXML_SUFFIX);
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Context.class.getResource(BASE_FXML_PATH));
		Parent root = (Parent) loader.load(fxmlStream);
		AbstractView controller = (AbstractView) loader.getController();
		controller.setRoot(root);
		controller.onViewInit();
		return controller;
	}

	public static ObservableList<Node> getObjectContainer() {
		return objectContainer;
	}

	public static void setObjectContainer(ObservableList<Node> objectContainer) {
		Context.objectContainer = objectContainer;
	}

}
