package agh.powerSim.simulation.actors.devices;

import agh.powerSim.simulation.actors.ClockActor;
import agh.powerSim.simulation.actors.House;
import agh.powerSim.simulation.actors.utils.CalculateUtils;
import agh.powerSim.simulation.actors.utils.DataRecorder;
import akka.actor.ActorRef;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDateTime;

public class Lamp extends BaseDevice {

	private double powerUsage = 10; // Watt
	private double lightGen = 10;
	private boolean isOn = false;
	
	public static boolean logOn = true;

	public Lamp(ActorRef house) {
		super(house);
	}

	public static List<DeviceType> getDeviceTypes() {
		ArrayList<DeviceType> deviceTypes = new ArrayList<DeviceType>(1);
		deviceTypes.add(DeviceType.LIGHT);
		return deviceTypes;
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof OnOffSignal) {
			OnOffSignal m = (OnOffSignal) message;
			isOn = m.state;
			// log.warning("state := " + isOn);
			if(logOn){
				getContext().actorFor("akka://SimSystem/user/recorder").tell(new DataRecorder.StatusRecord("OnOff status"+Boolean.toString(isOn), m.time, getSelf()), getSelf());
			}
			
		} else {
			super.onReceive(message);
		}
	}

	protected void onTime(ClockActor.TimeSignal t) {
		if (isOn) {
			double power = CalculateUtils.powerUsage(powerUsage, t.deltaTime);
			getHouse().tell(new House.PowerUsageSignal(power));

	
			double light = lightGen * t.deltaTime;
			getHouse().tell(new House.LightSignal(light));

			if(logOn){
				getContext().actorFor("akka://SimSystem/user/recorder").tell(new DataRecorder.PowerUsageRecord(power, light, t.time, getSelf()), getSelf());

				log.warning("Lamp is " + (isOn ? "ON" : "OFF"));
			}
		}
	}

	@Override
	public DeviceState getState() {
		return new DeviceState(isOn, isOn ? powerUsage : 0, isOn ? "ON" : "OFF", isOn ? "Device is ON" : "Device is OFF", getDeviceTypes());
	}

	public static class OnOffSignal {
		public final boolean state;
		public final LocalDateTime time;

		public OnOffSignal(boolean state, LocalDateTime time) {
			this.state = state;
			this.time = time;
		}
	}

}
