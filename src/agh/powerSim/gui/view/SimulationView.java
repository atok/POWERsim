package agh.powerSim.gui.view;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import agh.powerSim.WindowStarter;
import agh.powerSim.gui.Context;
import agh.powerSim.gui.core.AbstractView;

public class SimulationView extends AbstractView<AnchorPane> {

	public static final String PATH = "simulation/view";

	private ExecutorService exec = Executors.newFixedThreadPool(1);

	@FXML
	private HBox simulationObjects;

	public void runSimulation(ActionEvent event) {
		exec.execute(new Runnable() {

			@Override
			public void run() {
				WindowStarter.run();

			}
		});

	}

	@Override
	public void onViewLoad() {
	}

	@Override
	public void onViewInit() {
		// ustawia kontener do którego będą wrzucane ikonki obiektów symulacji
		Context.setObjectContainer(simulationObjects.getChildren());
	}

	public HBox getSimulationObjects() {
		return simulationObjects;
	}

	public void setSimulationObjects(HBox simulationObjects) {
		this.simulationObjects = simulationObjects;
	}

}
