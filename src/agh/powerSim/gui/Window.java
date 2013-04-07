package agh.powerSim.gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import agh.powerSim.gui.view.HomeView;
import agh.powerSim.gui.view.MenuView;

/**
 * klasa odpowiedzialna za utworzenie wątku javaFX i otwarcie pustego okna można
 * modygikować rozmiary okna, tytuł oraz pierwszy widok do załadowania
 * 
 * @author Darek
 * 
 */
public class Window extends Application {

	private static final String APP_TITLE = "powerSIM";

	private static final double WINDOW_WIDTH = 800;

	private static final double WINDOW_HEIGHT = 600;

	private static final String INIT_VIEW_PATH = HomeView.PATH;

	private static Stage stage;

	private static Logger log = Logger.getLogger(Window.class.getName());

	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		stage.setMinWidth(WINDOW_WIDTH);
		stage.setMinHeight(WINDOW_HEIGHT);
		stage.setScene(prepareScene());
		stage.setTitle(APP_TITLE);
		stage.initStyle(StageStyle.DECORATED);
		log.log(Level.ALL, "Application window ready to display");
		stage.show();
		Context.loadView(INIT_VIEW_PATH);
	}

	private Scene prepareScene() {
		Scene scene = new Scene(Context.getView(), WINDOW_WIDTH, WINDOW_HEIGHT);
		scene.setFill(Color.web("#BDBDBD"));
		MenuView menuController = (MenuView) Context.findView("menu");
		Context.getView().setTop(menuController.getRoot());
		log.log(Level.ALL, "Scene ready");
		return scene;
	}

	@Override
	public void stop() throws Exception {
		System.exit(0);
		super.stop();
	}
}
