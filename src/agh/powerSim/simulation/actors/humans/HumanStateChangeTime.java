package agh.powerSim.simulation.actors.humans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;


public class HumanStateChangeTime {

	public HumanState state;
	public List<Integer> dayOfWeekConstraints = new ArrayList<>();
	public List<Integer> monthOfYearConstraints = new ArrayList<>();
	public DateTime from;
	public DateTime till;
	public String comment = "";
	@SuppressWarnings("unchecked")
	public HumanStateChangeTime(HumanState state, DateTime from,DateTime till, Integer[] dayOfWeekConstraints, Integer[] monthOfYearCostraints, String comment){
		if(state!=HumanState.SLEEPING && state!=HumanState.OUTSIDE){
			this.state = HumanState.OUTSIDE;
		} else {
			this.state = state;
		}
		
		this.from = from;
		this.till = till;    		
		if(comment!=null)
			this.comment = comment;
	
		List<Integer> daysOfWeek = Arrays.asList(dayOfWeekConstraints);
		if(daysOfWeek==null){
			daysOfWeek = Collections.EMPTY_LIST;
		}
		if(!daysOfWeek.isEmpty()){
			if(daysOfWeek.get(0)>0){
				//only at days
				for(Integer dayOfWeek: daysOfWeek){
					this.dayOfWeekConstraints.add(Math.abs(dayOfWeek));
				}
			} else {
				//not on month
				this.dayOfWeekConstraints.addAll(Arrays.asList(new Integer[] {1,2,3,4,5,6,7}));
				for(Integer dayOfWeek: daysOfWeek){
					this.dayOfWeekConstraints.remove(this.dayOfWeekConstraints.indexOf(Math.abs(dayOfWeek)));
				}
				
			}
		} else {
			this.dayOfWeekConstraints.addAll(Arrays.asList(new Integer[] {1,2,3,4,5,6,7}));
		}
		

		List<Integer> monthsOfYear = Arrays.asList(monthOfYearCostraints);
		if(monthsOfYear==null){
			monthsOfYear = Collections.EMPTY_LIST;
		}
		if(!monthsOfYear.isEmpty()){
			if(monthsOfYear.get(0)>0){
				//only at month
				for(Integer monthOfYear: monthsOfYear){
					this.monthOfYearConstraints.add(Math.abs(monthOfYear));
				}
			} else {
				//not on month
				this.monthOfYearConstraints.addAll(Arrays.asList(new Integer[] {1,2,3,4,5,6,7,8,9,10,11,12}));
				for(Integer monthOfYear: monthsOfYear){
					this.monthOfYearConstraints.remove(this.monthOfYearConstraints.indexOf(Math.abs(monthOfYear)));
				}
				
			}
		} else {
			this.monthOfYearConstraints.addAll(Arrays.asList(new Integer[] {1,2,3,4,5,6,7,8,9,10,11,12}));
		}
	}
	
	public HumanStateChangeTime(HumanState state, DateTime from, int durationInMilis, Integer[] dayOfWeekConstraints, Integer[] dayOfMonthConstraints, String comment){
		this(state,from,from.plusMillis(durationInMilis), dayOfWeekConstraints,dayOfMonthConstraints ,comment);
	}
	
}
