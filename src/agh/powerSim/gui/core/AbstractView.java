package agh.powerSim.gui.core;

import javafx.scene.Parent;

public abstract class AbstractView<T extends Parent> {
	
	private T root;
	
	/**
	 * USED ONLY FOR VIEW INIT, SETS THE BASE PANE FOR FUTURE USE
	 * 
	 * @param root Top level pane for view
	 */
	public void setRoot(T root){
		this.root = root;
	}
	
	public T getRoot(){
		return root;
	}
	
	public abstract void onViewLoad();

	public abstract void onViewInit();
	
	
}
