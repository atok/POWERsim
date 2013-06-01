package agh.powerSim.simulation.actors.humans;

import java.util.Arrays;
import java.util.LinkedList;

public enum HumanState {
	SLEEPING, INSIDE, LEAVING, OUTSIDE, BEFORE_BEDTIME;
	
	private static LinkedList<HumanState> states;
	
	static {
		states= new LinkedList<>();
		states.addAll(Arrays.asList(HumanState.values()));
	}
	
	
	public HumanState prevState(){
		if(this.equals(BEFORE_BEDTIME)){
			return INSIDE;
		}
		int currentIdx = states.indexOf(this);
		int prevIdx;
		if(currentIdx==0){
			prevIdx = states.size()-1;
		} else {
			prevIdx=currentIdx-1;
		}
		return states.get(prevIdx);		
	};

	public HumanState nextState(){
		if(this.equals(OUTSIDE)){
			return INSIDE;
		}
		int currentIdx = states.indexOf(this);
		int nextIdx;
		if(currentIdx==states.size()-1){
			nextIdx = 0;
		} else {
			nextIdx=currentIdx+1;
		}
		return states.get(nextIdx);		
	};
	
};