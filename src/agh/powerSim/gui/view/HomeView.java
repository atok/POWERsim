package agh.powerSim.gui.view;

import javafx.event.ActionEvent;
import javafx.scene.layout.AnchorPane;
import agh.powerSim.gui.Context;
import agh.powerSim.gui.core.AbstractView;

public class HomeView extends AbstractView<AnchorPane>{

	public static final String PATH = "home/view";

	public void processLogin(ActionEvent event){
		Context.loadView(SimulationView.PATH);
	}

	@Override
	public void onViewLoad() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onViewInit() {
		// TODO Auto-generated method stub
		
	}
}
