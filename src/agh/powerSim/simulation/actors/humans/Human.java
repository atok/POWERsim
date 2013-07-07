package agh.powerSim.simulation.actors.humans;

import agh.powerSim.simulation.actors.ClockActor;
import agh.powerSim.simulation.actors.House;
import agh.powerSim.simulation.actors.devices.DeviceType;
import agh.powerSim.simulation.actors.devices.ElectricHeater;
import agh.powerSim.simulation.actors.devices.Lamp;
import agh.powerSim.simulation.actors.devices.TelevisionSet;
import agh.powerSim.simulation.actors.humans.BaseHuman.DeviceToken;
import agh.powerSim.simulation.actors.humans.HumanStateChanger.StateAndTime;
import akka.actor.ActorRef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeSet;

import org.joda.time.LocalDateTime;

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
		lightActions(report.light, report.deltaTime, report.time);
		radiatorActions(report.temperature, report.deltaTime, report.time);
        entertainmentActions(report.time);
	}

	private void radiatorActions(double temperature, double deltaTime, LocalDateTime time) {
		if (getState() == HumanState.INSIDE) {
			if (temperature < humanCharacter.warmComfortTreshold * deltaTime) {
				requestMoreHeat(time);
			} else if (temperature > humanCharacter.overheatComfortTreshold * deltaTime) {
				requestLessHeat(time);
			}
		} else if (getState() == HumanState.SLEEPING) {

		}

	}

	private void requestLessHeat(LocalDateTime time) {
		for (DeviceTokenWithState device : getDevices()) {
			if (device.is(DeviceType.HEATING) && device.state.isOn == true && device.stateChangeRequested == false) {
				device.actor.tell(new ElectricHeater.OnOffSignal(false, time), getSelf());
				break;
			}
		}
	}

	private void requestMoreHeat(LocalDateTime time) {
		for (DeviceTokenWithState device : getDevices()) {
			if (device.is(DeviceType.HEATING) && device.state.isOn == false && device.stateChangeRequested == false) {
				device.actor.tell(new ElectricHeater.OnOffSignal(true, time), getSelf());
				break;
			}
		}
	}

	private void requestMoreLight(LocalDateTime time) {
		for (DeviceTokenWithState device : getDevices()) {
			// log.debug("Device types: " + device.getTypesOfDevice());
			if (device.is(DeviceType.LIGHT) && device.state.isOn == false && device.stateChangeRequested == false) {
				device.actor.tell(new Lamp.OnOffSignal(true, time), getSelf());
				break;
			}
		}
	}

	private void requestLessLight(LocalDateTime time) {
		for (DeviceTokenWithState device : getDevices()) {
			// log.debug("Device types: " + device.getTypesOfDevice());
			if (device.is(DeviceType.LIGHT) && device.state.isOn == true && device.stateChangeRequested == false) {
				device.actor.tell(new Lamp.OnOffSignal(false, time), getSelf());
				break;
			}
		}

	}

	private void lightActions(double lightLevel, double deltaTime, LocalDateTime currentTime) {

		if (getState() == HumanState.INSIDE) {
			if (lightLevel < humanCharacter.lightComfortTreshold * deltaTime) {
				log.warning("More light!!!");
				requestMoreLight(currentTime);
			} else if (lightLevel > humanCharacter.lightOverloadTreshold * deltaTime) {
				log.warning("Less light!!!");
				requestLessLight(currentTime);
			}
		} else if (getState() == HumanState.BEFORE_BEDTIME) {
			if (isLastNotBussy()) {
				requestLessLight(currentTime);
			}
		} else if (getState() == HumanState.SLEEPING) {

		}
	}

    /**
     * Depending on the user entertainment attribute, he will turn on entertainment devices.
     * User is less likely to turn on encountered device if he turned on some devices a moment ago.
     *
     * Likelihood that user will turn off encountered entertainment device is inversely proportional to his entertainment character factor.
     *
     * @param time
     */
    private void entertainmentActions(LocalDateTime time) {
        if (getState() == HumanState.INSIDE) {
            Random random = new Random();
            int entertainmentMod = 0;

            for (DeviceTokenWithState device : getDevices()) {
                if(device.is(DeviceType.ENTERTAINMENT) && device.stateChangeRequested == false) {
                    if(device.state.isOn == false) {
                        if( (random.nextInt(100) + entertainmentMod) <= this.humanCharacter.getEntertainment()) {
                            log.warning("More fun!!!");
                            device.actor.tell(new TelevisionSet.OnOffSignal(true, time), getSelf());
                            entertainmentMod += 5;
                        }
                    } else {
                        if( (random.nextInt(100) + entertainmentMod) >= this.humanCharacter.getEntertainment()) {
                            log.warning("Less fun!!!");
                            device.actor.tell(new TelevisionSet.OnOffSignal(false, time), getSelf());
                        }
                    }
                }
            }
        } else if (getState() == HumanState.BEFORE_BEDTIME) {
            //TODO user may want to turn off devices before bedtime ;)
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
