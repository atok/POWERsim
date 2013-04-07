package agh.powerSim.gui.core;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;

public abstract class AbstractObjectView<T extends Parent> extends
		AbstractView<T> {
	
	private BooleanProperty active;

	public AbstractObjectView() {

		activeProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> list,
					Boolean oldVal, Boolean newVal) {
				if (newVal) {
					if (!getRoot().getStyleClass().contains("active")) {
						getRoot().getStyleClass().add("active");
					}
				} else {
					getRoot().getStyleClass().remove("active");
				}

			}
		});
	}

	public BooleanProperty activeProperty() {
		if (active == null) {
			active = new SimpleBooleanProperty(this, "active", false);
		}
		return active;
	}

	public boolean isActive() {
		return activeProperty().getValue();
	}

	public void setActive(boolean active) {
		activeProperty().set(active);
	}
}
