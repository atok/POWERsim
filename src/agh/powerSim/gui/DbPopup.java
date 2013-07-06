package agh.powerSim.gui;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Popup;

public class DbPopup {
	@FXML
	public AnchorPane root;

	Popup popup;

	public void open() {
		if (popup == null) {
			popup = new Popup();
			popup.setX(300);
			popup.setY(200);
			popup.getContent().add(root);

		}
		popup.show(Window.getStage());
	}
	
	public void close(ActionEvent event) {
		popup.hide();
	}
	
	public void clearDb(ActionEvent event) {
		File f = new File("powersim.db");
		if(f.exists()){
			f.delete();
		}
	}
}
