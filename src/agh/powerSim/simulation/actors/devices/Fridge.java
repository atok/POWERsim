package agh.powerSim.simulation.actors.devices;

import java.util.ArrayList;
import java.util.List;

import agh.powerSim.simulation.actors.ClockActor.TimeSignal;
import agh.powerSim.simulation.actors.utils.CalculateUtils;
import agh.powerSim.simulation.actors.utils.DataRecorder;
import akka.actor.ActorRef;

public class Fridge extends BaseDevice {

	public static boolean logOn = true;

	public int standByPower = 70;
	public int runPower = 1800;

	public int maxStepToTurnOn = 120;
	public int currStepsToTurnOn = maxStepToTurnOn;

	private final int RELOAD_VALUE = 10;
	private final int OPEN_PENALTY = 30;

	private boolean isOn = false;

	public Fridge(ActorRef house) {
		super(house);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof BaseDevice.OnOffSignal) {
			currStepsToTurnOn -= OPEN_PENALTY;

			if (logOn) {
				getContext().actorFor("akka://SimSystem/user/recorder").tell(new DataRecorder.StatusRecord("Fridge door opened", ((BaseDevice.OnOffSignal) message).time, getSelf()), getSelf());
			}
		} else {
			super.onReceive(message);
		}
	}

	@Override
	protected void onTime(TimeSignal t) {
		if (currStepsToTurnOn-- < 0) {
			// turn on
			currStepsToTurnOn = 0;
			isOn = true;
			if (logOn) {
				double power = CalculateUtils.powerUsage(runPower, t.deltaTime);
				getContext().actorFor("akka://SimSystem/user/recorder").tell(new DataRecorder.StatusRecord("Fridge is cooling", t.time, getSelf()), getSelf());

				getContext().actorFor("akka://SimSystem/user/recorder").tell(new DataRecorder.PowerUsageRecord(power, 0, t.time, getSelf()), getSelf());

				log.warning("Frindge is running");
			}
		} else if (currStepsToTurnOn < maxStepToTurnOn && isOn) {
			// refrize
			currStepsToTurnOn += RELOAD_VALUE;

			if (logOn) {
				double power = CalculateUtils.powerUsage(runPower, t.deltaTime);
				getContext().actorFor("akka://SimSystem/user/recorder").tell(new DataRecorder.PowerUsageRecord(power, 0, t.time, getSelf()), getSelf());

				log.warning("Frindge is running");
			}
		} else {
			if (isOn) {
				isOn = false;

				if (logOn) {
					getContext().actorFor("akka://SimSystem/user/recorder").tell(new DataRecorder.StatusRecord("Fridge is not cooling", t.time, getSelf()), getSelf());
				}
			}
			// turn off
			if (logOn) {

				double power = CalculateUtils.powerUsage(standByPower, t.deltaTime);
				getContext().actorFor("akka://SimSystem/user/recorder").tell(new DataRecorder.PowerUsageRecord(power, 0, t.time, getSelf()), getSelf());

				log.warning("Frindge in standBy");

			}
		}

	}

	public static List<DeviceType> getDeviceTypes() {
		ArrayList<DeviceType> deviceTypes = new ArrayList<>(1);
		deviceTypes.add(DeviceType.MEAL);
		return deviceTypes;
	}

	@Override
	public BaseDevice.DeviceState getState() {
		return new BaseDevice.DeviceState(isOn, isOn ? runPower : standByPower, isOn ? "ON" : "OFF", isOn ? "Device is ON" : "Device is OFF", getDeviceTypes());
	}

}
