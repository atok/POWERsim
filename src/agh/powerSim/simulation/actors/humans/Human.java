package agh.powerSim.simulation.actors.humans;

import agh.powerSim.simulation.actors.ClockActor;
import agh.powerSim.simulation.actors.House;
import agh.powerSim.simulation.actors.devices.DeviceType;
import agh.powerSim.simulation.actors.devices.ElectricHeater;
import agh.powerSim.simulation.actors.devices.Lamp;
import agh.powerSim.simulation.actors.humans.BaseHuman.DeviceToken;
import agh.powerSim.simulation.actors.humans.HumanStateChanger.StateAndTime;
import akka.actor.ActorRef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class Human extends BaseHuman {

	ClockActor.TimeSignal currentTime;

	public Human(ActorRef house, ArrayList<DeviceToken> devices, HumanCharacter humanCharacter, HumanStateChangeTime[] stateChanges) {
		super(house, devices, humanCharacter, stateChanges);
	}

	public Human(ActorRef house, ArrayList<DeviceToken> devices, HumanCharacter humanCharacter) {
		super(house, devices, humanCharacter);
	}

	public Human(ActorRef house, ArrayList<DeviceToken> devices) {
		super(house, devices);
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof SomeCustomEvent) {
			// EXAMPLE
		} else if (message instanceof HumanStateNotice) {
			HumanStateNotice msg = (HumanStateNotice) message;
			if (!msg.sender.equals(getSelf())) {
				getHousemates().put(msg.sender, msg.stateAndTime);
			}
		} else {
			super.onReceive(message);
		}
	}

	@Override
	protected void onTime(ClockActor.TimeSignal timeSignal) {
		log.info("MY STATE is " + getState());
		currentTime = timeSignal;
		requestDevicesStateUpdate();
	}

	@Override
	protected void onHouseState(House.StateReport report) {
		lightActions(report.light, report.deltaTime);
		radiatorActions(report.temperature, report.deltaTime);
	}

	private void radiatorActions(double temperature, double deltaTime) {
		if (getState() == HumanState.INSIDE) {
			if (temperature < humanCharacter.warmComfortTreshold * deltaTime) {
				requestMoreHeat();
			} else if (temperature > humanCharacter.overheatComfortTreshold * deltaTime) {
				requestLessHeat();
			}
		} else if (getState() == HumanState.SLEEPING) {

		}

	}

	private void requestLessHeat() {
		for (DeviceTokenWithState device : getDevices()) {
			if (device.is(DeviceType.HEATING) && device.state.isOn == true && device.stateChangeRequested == false) {
				device.actor.tell(new ElectricHeater.OnOffSignal(false), getSelf());
				break;
			}
		}
	}

	private void requestMoreHeat() {
		for (DeviceTokenWithState device : getDevices()) {
			if (device.is(DeviceType.HEATING) && device.state.isOn == false && device.stateChangeRequested == false) {
				device.actor.tell(new ElectricHeater.OnOffSignal(true), getSelf());
				break;
			}
		}
	}

	private void requestMoreLight() {
		for (DeviceTokenWithState device : getDevices()) {
			// log.debug("Device types: " + device.getTypesOfDevice());
			if (device.is(DeviceType.LIGHT) && device.state.isOn == false && device.stateChangeRequested == false) {
				device.actor.tell(new Lamp.OnOffSignal(true), getSelf());
				break;
			}
		}
	}

	private void requestLessLight() {
		for (DeviceTokenWithState device : getDevices()) {
			// log.debug("Device types: " + device.getTypesOfDevice());
			if (device.is(DeviceType.LIGHT) && device.state.isOn == true && device.stateChangeRequested == false) {
				device.actor.tell(new Lamp.OnOffSignal(false), getSelf());
				break;
			}
		}

	}

	private void lightActions(double lightLevel, double deltaTime) {

		if (getState() == HumanState.INSIDE) {
			if (lightLevel < humanCharacter.lightComfortTreshold * deltaTime) {
				log.warning("More light!!!");
				requestMoreLight();
			} else if (lightLevel > humanCharacter.lightOverloadTreshold * deltaTime) {
				log.warning("Less light!!!");
				requestLessLight();
			}
		} else if (getState() == HumanState.BEFORE_BEDTIME) {
			if (isLastNotBussy()) {
				requestLessLight();
			}
		} else if (getState() == HumanState.SLEEPING) {

		}
	}

	public int getLightComfortTreshold() {
		return humanCharacter.lightComfortTreshold;
	}

	public void setLightComfortTreshold(int lightComfortTreshold) {
		this.humanCharacter.lightComfortTreshold = lightComfortTreshold;
	}

	public static class SomeCustomEvent {
	}

	public static class HumanStateNotice {
		public ActorRef sender;

		public StateAndTime stateAndTime;

		public HumanStateNotice(ActorRef sender, StateAndTime stateAndTime) {
			this.sender = sender;
			this.stateAndTime = stateAndTime;
		}
	}

}
