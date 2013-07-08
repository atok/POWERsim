package agh.powerSim.simulation.actors.humans;

import agh.powerSim.simulation.actors.ClockActor;
import agh.powerSim.simulation.actors.House;
import agh.powerSim.simulation.actors.devices.BaseDevice;
import agh.powerSim.simulation.actors.devices.DeviceType;
import agh.powerSim.simulation.actors.devices.ElectricHeater;
import agh.powerSim.simulation.actors.devices.Lamp;
import agh.powerSim.simulation.actors.devices.TelevisionSet;
import agh.powerSim.simulation.actors.humans.BaseHuman.DeviceToken;
import agh.powerSim.simulation.actors.humans.HumanStateChanger.StateAndTime;
import agh.powerSim.simulation.actors.utils.DataRecorder;
import akka.actor.ActorRef;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeSet;

import org.joda.time.LocalDateTime;

public class Human extends BaseHuman {

	ClockActor.TimeSignal currentTime;
	public static boolean logOn = true;

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
		if (logOn)
			log.info("MY STATE is " + getState());
		currentTime = timeSignal;
		humanCharacter.hunger -= 5;
		requestDevicesStateUpdate();
	}

	@Override
	protected void onHouseState(House.StateReport report) {
		if (!foodActions(report.deltaTime, report.time)) {
			if (!cleaningAction(report.deltaTime, report.time)) {
				if (!entertainmentActions(report.time)) {

					lightActions(report.light, report.deltaTime, report.time);
					radiatorActions(report.temperature, report.deltaTime, report.time);

				}
			}
		}

	}

	private boolean cleaningAction(double deltaTime, LocalDateTime time) {
		if (getState() == HumanState.INSIDE) {
			Random r = new Random(time.getMillisOfDay());
			if (r.nextInt(1000) > 900) {
				for (DeviceTokenWithState device : getDevices()) {
					if (device.is(DeviceType.CLEANING)) {
						device.actor.tell(new BaseDevice.OnOffSignal(true, time), getSelf());
						if (logOn) {
							getContext().actorFor("akka://SimSystem/user/recorder").tell(new DataRecorder.StatusRecord("Cleaning with " + device.actor.path(), time, getSelf()), getSelf());
							log.warning("Cleaning with " + device.actor.path());
						}
						return true;

					}
				}
			}
		}
		return false;
	}

	private boolean foodActions(double deltaTime, LocalDateTime time) {
		if (getState() == HumanState.INSIDE) {
			if (humanCharacter.getHunger() <= 0) {
				for (DeviceTokenWithState device : getDevices()) {
					if (device.is(DeviceType.MEAL)) {
						device.actor.tell(new BaseDevice.OnOffSignal(true, time), getSelf());
						Random r = new Random(Calendar.getInstance().getTimeInMillis());
						humanCharacter.hunger += 400 + r.nextInt(500);
						if (logOn) {
							getContext().actorFor("akka://SimSystem/user/recorder").tell(new DataRecorder.StatusRecord("Eating using " + device.actor.path(), time, getSelf()), getSelf());
							log.warning("Eating using " + device.actor.path());
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean radiatorActions(double temperature, double deltaTime, LocalDateTime time) {
		if (getState() == HumanState.INSIDE) {
			if (temperature < humanCharacter.warmComfortTreshold * deltaTime) {
				requestMoreHeat(time);
				return true;
			} else if (temperature > humanCharacter.overheatComfortTreshold * deltaTime) {
				requestLessHeat(time);
				return true;
			}
		} else if (getState() == HumanState.SLEEPING) {

		}
		return false;

	}

	private void requestLessHeat(LocalDateTime time) {
		for (DeviceTokenWithState device : getDevices()) {
			if (device.is(DeviceType.HEATING) && device.state.isOn == true && device.stateChangeRequested == false) {
				device.actor.tell(new ElectricHeater.OnOffSignal(false, time), getSelf());

				if (logOn) {
					getContext().actorFor("akka://SimSystem/user/recorder").tell(new DataRecorder.StatusRecord("Radiator action " + device.actor.path(), time, getSelf()), getSelf());
					log.warning("Radiator action " + device.actor.path());
				}
				break;
			}
		}
	}

	private void requestMoreHeat(LocalDateTime time) {
		for (DeviceTokenWithState device : getDevices()) {
			if (device.is(DeviceType.HEATING) && device.state.isOn == false && device.stateChangeRequested == false) {
				device.actor.tell(new ElectricHeater.OnOffSignal(true, time), getSelf());

				if (logOn) {
					getContext().actorFor("akka://SimSystem/user/recorder").tell(new DataRecorder.StatusRecord("Radiator actioh " + device.actor.path(), time, getSelf()), getSelf());
					log.warning("Radiator action " + device.actor.path());
				}
				break;
			}
		}
	}

	private void requestMoreLight(LocalDateTime time) {
		for (DeviceTokenWithState device : getDevices()) {
			// log.debug("Device types: " + device.getTypesOfDevice());
			if (device.is(DeviceType.LIGHT) && device.state.isOn == false && device.stateChangeRequested == false) {
				device.actor.tell(new Lamp.OnOffSignal(true, time), getSelf());

				if (logOn) {
					getContext().actorFor("akka://SimSystem/user/recorder").tell(new DataRecorder.StatusRecord("Light action " + device.actor.path(), time, getSelf()), getSelf());
					log.warning("Light action " + device.actor.path());
				}
				break;
			}
		}
	}

	private void requestLessLight(LocalDateTime time) {
		for (DeviceTokenWithState device : getDevices()) {
			// log.debug("Device types: " + device.getTypesOfDevice());
			if (device.is(DeviceType.LIGHT) && device.state.isOn == true && device.stateChangeRequested == false) {
				device.actor.tell(new Lamp.OnOffSignal(false, time), getSelf());

				if (logOn) {
					getContext().actorFor("akka://SimSystem/user/recorder").tell(new DataRecorder.StatusRecord("Light action " + device.actor.path(), time, getSelf()), getSelf());
					log.warning("Light action " + device.actor.path());
				}
				break;
			}
		}

	}

	private boolean lightActions(double lightLevel, double deltaTime, LocalDateTime currentTime) {

		if (getState() == HumanState.INSIDE) {
			if (lightLevel < humanCharacter.lightComfortTreshold * deltaTime) {
				if (logOn)
					log.warning("More light!!!");
				requestMoreLight(currentTime);
				return true;
			} else if (lightLevel > humanCharacter.lightOverloadTreshold * deltaTime) {
				if (logOn)
					log.warning("Less light!!!");
				requestLessLight(currentTime);
				return true;
			}
		} else if (getState() == HumanState.BEFORE_BEDTIME) {
			if (isLastNotBussy()) {
				requestLessLight(currentTime);
				return true;
			}
		} else if (getState() == HumanState.SLEEPING) {

		}
		return false;
	}

	/**
	 * Depending on the user entertainment attribute, he will turn on
	 * entertainment devices. User is less likely to turn on encountered device
	 * if he turned on some devices a moment ago.
	 * 
	 * Likelihood that user will turn off encountered entertainment device is
	 * inversely proportional to his entertainment character factor.
	 * 
	 * @param time
	 */
	private boolean entertainmentActions(LocalDateTime time) {
		if (getState() == HumanState.INSIDE) {
			Random random = new Random();
			int entertainmentMod = 0;

			for (DeviceTokenWithState device : getDevices()) {
				if (device.is(DeviceType.ENTERTAINMENT) && device.stateChangeRequested == false) {
					if (device.state.isOn == false) {
						if ((random.nextInt(100) + entertainmentMod) <= this.humanCharacter.getEntertainment()) {
							if (logOn)
								log.warning("More fun!!!");
							device.actor.tell(new TelevisionSet.OnOffSignal(true, time), getSelf());
							entertainmentMod += 5;
							if (logOn) {
								getContext().actorFor("akka://SimSystem/user/recorder").tell(new DataRecorder.StatusRecord("More fun with " + device.actor.path(), time, getSelf()), getSelf());

							}
							return true;
						}
					} else {
						if ((random.nextInt(100) + entertainmentMod) >= this.humanCharacter.getEntertainment()) {
							if (logOn)
								log.warning("Less fun!!!");
							device.actor.tell(new TelevisionSet.OnOffSignal(false, time), getSelf());

							if (logOn) {
								getContext().actorFor("akka://SimSystem/user/recorder").tell(new DataRecorder.StatusRecord("Disable fun on " + device.actor.path(), time, getSelf()), getSelf());
							}
							return true;
						}
					}
				}
			}
		} else if (getState() == HumanState.BEFORE_BEDTIME) {
			// TODO user may want to turn off devices before bedtime ;)
		}
		return false;
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
