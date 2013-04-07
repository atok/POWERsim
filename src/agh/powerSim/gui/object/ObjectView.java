package agh.powerSim.gui.object;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import agh.powerSim.gui.core.AbstractObjectView;

public class ObjectView extends AbstractObjectView<AnchorPane> {

	@FXML
	private ImageView image;
	
	@Override
	public void onViewLoad() {

	}

	@Override
	public void onViewInit() {

	}

	public ImageView getImage() {
		return image;
	}

	public void setImage(ImageView image) {
		this.image = image;
	}

}
