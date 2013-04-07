package agh.powerSim.simulation.actors.devices;

import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import agh.powerSim.gui.Context;
import agh.powerSim.gui.object.ObjectView;
import agh.powerSim.gui.view.SimulationView;
import akka.actor.ActorRef;

public class LampView extends Lamp {

	private ObjectView view;

	public LampView(ActorRef house) {
		super(house);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				view = (ObjectView) Context.objectFactory("lamp");
			}
		});
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof OnOffSignal) {
			final OnOffSignal m = (OnOffSignal) message;
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					view.setActive(m.state);
				}
			});
		}
		super.onReceive(message);
	}

}
