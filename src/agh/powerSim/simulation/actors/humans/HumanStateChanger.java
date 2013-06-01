package agh.powerSim.simulation.actors.humans;

import java.util.ArrayList;
import java.util.Arrays;

import org.joda.time.DateTime;

import agh.powerSim.simulation.actors.ClockActor;

public class HumanStateChanger {

	private final ArrayList<HumanStateChangeTime> stateChanges = new ArrayList<>();

	private HumanState currentState;

	private DateTime currentFromDate;
	
	private boolean active = false;

	private DateTime tillTime;

	private DateTime fromTime;

	private HumanState transformingState;

	public void addStateChange(HumanStateChangeTime stateChangeTime) {
		stateChanges.add(stateChangeTime);
	}

	public void addAllStateChenges(HumanStateChangeTime[] stateChangeTimes) {
		stateChanges.addAll(Arrays.asList(stateChangeTimes));
	}

	public void processTime(ClockActor.TimeSignal time) {
		processTime(time, false);
	}

	private void processTime(ClockActor.TimeSignal time, boolean dayBefore) {
		DateTime current = time.time.toDateTime();
		if (currentState == null && !dayBefore) {
			currentState = HumanState.INSIDE;
			processTime(time, true);
		}
		if (active) {
			DateTime tmp = fromTime.minusSeconds(15 * 60 > (int) time.deltaTime ? 15 * 60 : (int) time.deltaTime);
			if ((current.isEqual(fromTime) || current.isAfter(fromTime)) && current.isBefore(tillTime)) {
				currentState = transformingState;
				currentFromDate = fromTime;
			} else if ((current.isEqual(tmp) || current.isAfter(tmp)) && current.isBefore(tillTime)) {
				currentState = transformingState.prevState();
				currentFromDate = tmp;
			} else {
				currentState = transformingState.nextState();
				currentFromDate = tillTime;
				active = false;
			}
			return;
		}
		for (HumanStateChangeTime changeTime : stateChanges) {
			if (active)
				return;
			int dayOfWeek = current.dayOfWeek().get();
			if (!changeTime.dayOfWeekConstraints.contains(dayOfWeek)) {
				continue;
			}
			int monthOfYear = current.monthOfYear().get();
			if (!changeTime.monthOfYearConstraints.contains(monthOfYear)) {
				continue;
			}
			DateTime fromTime = new DateTime(current.getYear(), current.getMonthOfYear(), current.getDayOfMonth(), changeTime.from.getHourOfDay(), changeTime.from.getMinuteOfHour());
			DateTime tillTime = new DateTime(current.getYear(), current.getMonthOfYear(), current.getDayOfMonth(), changeTime.till.getHourOfDay(), changeTime.till.getMinuteOfHour());
			if (fromTime.isAfter(tillTime)) {
				tillTime = tillTime.plusDays(1);
			}
			if (dayBefore) {
				fromTime = fromTime.minusDays(1);
				tillTime = tillTime.minusDays(1);
			}
			if ((current.isEqual(fromTime.minusSeconds(15 * 60 > (int) time.deltaTime ? 15 * 60 : (int) time.deltaTime)) || current.isAfter(fromTime.minusSeconds(15 * 60 > (int) time.deltaTime ? 15 * 60 : (int) time.deltaTime)))
					&& current.isBefore(tillTime)) {
				active = true;
				this.fromTime = fromTime;
				this.tillTime = tillTime;
				this.transformingState = changeTime.state;
			}

		}

	}

	public HumanState getCurrentState() {
		return currentState;
	}

	public DateTime getCurrentFromDate() {
		return currentFromDate;
	}
	
	public StateAndTime getCurrentStateAndTime(){
		return new StateAndTime(getCurrentState(), getCurrentFromDate());
	}

	public static class StateAndTime{
		public HumanState state;
		public DateTime time;
		public StateAndTime(HumanState state, DateTime time){
			this.state=state;
			this.time=time;
		}
	}
}
