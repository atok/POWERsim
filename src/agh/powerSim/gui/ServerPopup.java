package agh.powerSim.gui;

import agh.powerSim.JettyStarter;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Popup;

public class ServerPopup {

	@FXML
	public AnchorPane root;

	@FXML
	public TextField port;

	@FXML
	public TextField sim;

	@FXML
	public TextField data;

	@FXML
	public Button start;
	
	@FXML
	public Button kill;

	@FXML
	public ProgressIndicator status;

	Popup popup;

	private boolean isRun = false;

	public void open() {
		if (popup == null) {
			popup = new Popup();
			popup.setX(300);
			popup.setY(200);
			popup.getContent().add(root);
			
			sim.setText(JettyStarter.getSimulationsUrl);
			data.setText(JettyStarter.getSimulationsGetDataUrl);
			port.setText(Integer.toString(JettyStarter.port));
		}
		popup.show(Window.getStage());
	}

	public void close(ActionEvent event) {
		popup.hide();
	}

	public void action(ActionEvent event) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {

				if (isRun) {
					status.setProgress(0);
					status.setVisible(false);
					start.setText("Start");
					port.setDisable(false);
					sim.setDisable(false);
					data.setDisable(false);
					kill.setDisable(false);
					
					Context.stopServer();
				} else {

					status.setProgress(-1.0);
					status.setVisible(true);
					start.setText("Stop");
					port.setDisable(true);
					sim.setDisable(true);
					data.setDisable(true);
					kill.setDisable(true);
					
					Context.runServer();
				}
				isRun=!isRun;
				
			}
		});
	}
}
