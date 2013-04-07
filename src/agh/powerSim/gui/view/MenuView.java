package agh.powerSim.gui.view;

import agh.powerSim.gui.core.AbstractView;
import javafx.scene.control.MenuBar;
/**
 * Kontroler menu górnego -> widok znajduje się w fxml/menu.fxml
 * @author Darek
 *
 */
public class MenuView extends AbstractView<MenuBar> {
	public void close() {
		System.exit(0);
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
