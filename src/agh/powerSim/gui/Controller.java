package agh.powerSim.gui;


import java.io.File;

import com.typesafe.config.Config;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class Controller {

	@FXML
	private Parent root;

	@FXML
	private Label fileLabel;
	
	@FXML 
	TextField delay;
	
	@FXML
	TextField duration;
	
	@FXML
	private Label alertBox;
	
	@FXML
	Button startButton;
	
	@FXML
	Label weatherIsSun;
	
	@FXML 
	Label weatherTemp;
	
	@FXML
	Label weatherClouds;
	
	@FXML
	Label simulationDate;
	
	@FXML
	Button openButton;
	
	private  File file;
	
	public int getDelay(){
		return Integer.parseInt(delay.getText());
	}
	
	public int getDuration(){
		return Integer.parseInt(duration.getText());
	}
	
	public Parent getRoot() {
		return root;
	}

	public void setRoot(Parent root) {
		this.root = root;
	}

	
	public void close(ActionEvent event){
		System.exit(0);
	}
	
	public void open(ActionEvent event){
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
       
        //Show open file dialog
        file = fileChooser.showOpenDialog(null);
       
        if(file!=null)
        	fileLabel.setText(file.getName());
	}
	
	public void run(ActionEvent event){
		if(file!=null && getDelay()>0 && getDuration()>0){
			alertBox.setVisible(false);
			delay.setDisable(true);
			duration.setDisable(true);
			startButton.setText("Stop");
			startButton.setDisable(true);
			openButton.setDisable(true);
			Context.runSimulation(file,getDuration(),getDelay());
		} else {
			alertBox.setText("Invalid start data");
			alertBox.setVisible(true);			
		}
	}
}
