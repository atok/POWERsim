package agh.powerSim.gui;

import java.io.File;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import agh.powerSim.simulation.actors.ClockActor;
import agh.powerSim.simulation.actors.House;
import agh.powerSim.simulation.actors.devices.DishWasher;
import agh.powerSim.simulation.actors.devices.ElectricHeater;
import agh.powerSim.simulation.actors.devices.ElectricKettle;
import agh.powerSim.simulation.actors.devices.Fridge;
import agh.powerSim.simulation.actors.devices.Iron;
import agh.powerSim.simulation.actors.devices.Lamp;
import agh.powerSim.simulation.actors.devices.MicrowaveOven;
import agh.powerSim.simulation.actors.devices.MobileDevice;
import agh.powerSim.simulation.actors.devices.Oven;
import agh.powerSim.simulation.actors.devices.RadioSet;
import agh.powerSim.simulation.actors.devices.TelevisionSet;
import agh.powerSim.simulation.actors.devices.VacumCliner;
import agh.powerSim.simulation.actors.devices.WashingMashine;
import agh.powerSim.simulation.actors.devices.WaterHeater;
import agh.powerSim.simulation.actors.environment.WeatherActor;
import agh.powerSim.simulation.actors.humans.Human;

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

	@FXML
	Parent logging;

	@FXML
	CheckBox defaultSim;

	ServerPopup serverPopup;

	DbPopup dbPopup;

	WeatherPopup weatherPopup;

	private File file;

	public void disableAll(boolean disable) {
		for (Node n : logging.getChildrenUnmodifiable()) {
			n.setDisable(disable);
		}
	}

	public int getDelay() {
		return Integer.parseInt(delay.getText());
	}

	public int getDuration() {
		return Integer.parseInt(duration.getText());
	}

	public Parent getRoot() {
		return root;
	}

	public void setRoot(Parent root) {
		this.root = root;
	}

	public void close(ActionEvent event) {
		System.exit(0);
	}

	public void open(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();

		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON", "*.json");
		fileChooser.getExtensionFilters().add(extFilter);

		// Show open file dialog
		file = fileChooser.showOpenDialog(null);

		if (file != null)
			fileLabel.setText(file.getName());
	}

	public void run(ActionEvent event) {
		if (defaultSim.isSelected()) {
			alertBox.setVisible(false);
			delay.setDisable(true);
			duration.setDisable(true);
			startButton.setText("Stop");
			startButton.setDisable(true);
			openButton.setDisable(true);
			disableAll(true);
			Context.runDefSimulation(getDuration(), getDelay());
		} else {
			if (file != null && getDelay() > 0 && getDuration() > 0) {
				alertBox.setVisible(false);
				delay.setDisable(true);
				duration.setDisable(true);
				startButton.setText("Stop");
				startButton.setDisable(true);
				openButton.setDisable(true);
				disableAll(true);
				Context.runSimulation(file, getDuration(), getDelay());
			} else {
				alertBox.setText("Invalid start data");
				alertBox.setVisible(true);
			}
		}
	}

	public void weatherPopup(ActionEvent event) {
		if (weatherPopup == null) {
			try {
				weatherPopup = Context.loadWeatherPopup();

				weatherPopup.open();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			weatherPopup.open();
		}
	}

	public void serverPopup(ActionEvent event) {
		if (serverPopup == null) {
			try {
				serverPopup = Context.loadServerPopup();

				serverPopup.open();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			serverPopup.open();
		}
	}

	public void dbPopup(ActionEvent event) {
		if (dbPopup == null) {
			try {
				dbPopup = Context.loadDbPopup();

				dbPopup.open();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			dbPopup.open();
		}
	}

	public void clock(ActionEvent e) {
		if (e.getSource() instanceof CheckBox) {
			CheckBox c = (CheckBox) e.getSource();

			ClockActor.logOn = c.isSelected();

		}
	}

	public void weather(ActionEvent e) {
		if (e.getSource() instanceof CheckBox) {
			CheckBox c = (CheckBox) e.getSource();

			WeatherActor.logOn = c.isSelected();

		}
	}

	public void house(ActionEvent e) {
		if (e.getSource() instanceof CheckBox) {
			CheckBox c = (CheckBox) e.getSource();

			House.logOn = c.isSelected();

		}
	}

	public void human(ActionEvent e) {
		if (e.getSource() instanceof CheckBox) {
			CheckBox c = (CheckBox) e.getSource();

			Human.logOn = c.isSelected();

		}
	}

	public void dw(ActionEvent e) {
		if (e.getSource() instanceof CheckBox) {
			CheckBox c = (CheckBox) e.getSource();

			DishWasher.logOn = c.isSelected();

		}
	}

	public void eh(ActionEvent e) {
		if (e.getSource() instanceof CheckBox) {
			CheckBox c = (CheckBox) e.getSource();

			ElectricHeater.logOn = c.isSelected();

		}
	}

	public void ek(ActionEvent e) {
		if (e.getSource() instanceof CheckBox) {
			CheckBox c = (CheckBox) e.getSource();

			ElectricKettle.logOn = c.isSelected();

		}
	}

	public void fridge(ActionEvent e) {
		if (e.getSource() instanceof CheckBox) {
			CheckBox c = (CheckBox) e.getSource();

			Fridge.logOn = c.isSelected();

		}
	}

	public void iron(ActionEvent e) {
		if (e.getSource() instanceof CheckBox) {
			CheckBox c = (CheckBox) e.getSource();

			Iron.logOn = c.isSelected();

		}
	}

	public void lamp(ActionEvent e) {
		if (e.getSource() instanceof CheckBox) {
			CheckBox c = (CheckBox) e.getSource();

			Lamp.logOn = c.isSelected();

		}
	}

	public void micro(ActionEvent e) {
		if (e.getSource() instanceof CheckBox) {
			CheckBox c = (CheckBox) e.getSource();

			MicrowaveOven.logOn = c.isSelected();

		}
	}

	public void mobileDevice(ActionEvent e) {
		if (e.getSource() instanceof CheckBox) {
			CheckBox c = (CheckBox) e.getSource();

			MobileDevice.logOn = c.isSelected();

		}
	}

	public void oven(ActionEvent e) {
		if (e.getSource() instanceof CheckBox) {
			CheckBox c = (CheckBox) e.getSource();

			Oven.logOn = c.isSelected();

		}
	}

	public void radio(ActionEvent e) {
		if (e.getSource() instanceof CheckBox) {
			CheckBox c = (CheckBox) e.getSource();

			RadioSet.logOn = c.isSelected();

		}
	}

	public void tv(ActionEvent e) {
		if (e.getSource() instanceof CheckBox) {
			CheckBox c = (CheckBox) e.getSource();

			TelevisionSet.logOn = c.isSelected();

		}
	}

	public void wm(ActionEvent e) {
		if (e.getSource() instanceof CheckBox) {
			CheckBox c = (CheckBox) e.getSource();

			WashingMashine.logOn = c.isSelected();

		}
	}

	public void wh(ActionEvent e) {
		if (e.getSource() instanceof CheckBox) {
			CheckBox c = (CheckBox) e.getSource();

			WaterHeater.logOn = c.isSelected();

		}
	}

	public void vc(ActionEvent e) {
		if (e.getSource() instanceof CheckBox) {
			CheckBox c = (CheckBox) e.getSource();

			VacumCliner.logOn = c.isSelected();

		}
	}
}
