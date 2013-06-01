package agh.powerSim.simulation.actors;

import java.util.HashMap;

import agh.powerSim.simulation.actors.humans.Human;
import agh.powerSim.simulation.actors.humans.HumanState;
import agh.powerSim.simulation.actors.humans.HumanStateChanger;
import agh.powerSim.simulation.actors.humans.HumanStateChanger.StateAndTime;
import agh.powerSim.simulation.actors.utils.DataRecorder;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class House extends UntypedActor {

	HashMap<ActorRef, HumanStateChanger.StateAndTime> humans = new HashMap<>();

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	double powerUsedInThisStep = 0;
	double lightProvidedInThisStep = 0;
	double heatProvidedInThisStep = 0;

	double houseIsolation = 0.5;

	@Override
	public void preStart() {
		super.preStart();
		getContext().actorFor("akka://SimSystem/user/clock").tell(new ClockActor.RegisterActorSignal(getSelf(), House.class), getSelf());
		log.error("house started");
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof ClockActor.TimeSignal) {
			ClockActor.TimeSignal t = (ClockActor.TimeSignal) message;

			getContext().actorFor("akka://SimSystem/user/recorder").tell(new DataRecorder.PowerUsageRecord(powerUsedInThisStep, lightProvidedInThisStep, t, getSelf()), getSelf());

			ActorRef last = findLastHuman();
			for (ActorRef human : humans.keySet()) {
				boolean lastNotBussyHuman = false;
				if (last.equals(human)) {
					lastNotBussyHuman = true;
				}
				human.tell(new StateReport(lightProvidedInThisStep, heatProvidedInThisStep, t.deltaTime, humans, lastNotBussyHuman), getSelf());
			}

			powerUsedInThisStep = 0;
			lightProvidedInThisStep = 0;
			heatProvidedInThisStep = 0;

			getSender().tell(new ClockActor.DoneSignal(), getSelf());

		} else if (message instanceof PowerUsageSignal) {
			PowerUsageSignal powerSignal = (PowerUsageSignal) message;
			powerUsedInThisStep += powerSignal.powerUsed;
		} else if (message instanceof LightSignal) {
			LightSignal lightSignal = (LightSignal) message;
			lightProvidedInThisStep += lightSignal.light;
			// log.warning("LIGHT: "+lightProvidedInThisStep);
		} else if (message instanceof HeatSignal) {
			HeatSignal heatSignal = (HeatSignal) message;
			if (heatSignal.weather) {
				heatProvidedInThisStep += heatSignal.heat * (1 - houseIsolation);
			} else {
				heatProvidedInThisStep += heatSignal.heat;
			}
			// log.warning("HEAT: "+heatProvidedInThisStep);
		} else if (message instanceof RegisterForState) {
			humans.put(getSender(), new HumanStateChanger.StateAndTime(null, null));
		} else if (message instanceof Human.HumanStateNotice) {
			Human.HumanStateNotice msg = (Human.HumanStateNotice) message;
			humans.put(msg.sender, msg.stateAndTime);
		} else {
			unhandled(message);
		}
	}

	private ActorRef findLastHuman() {
		ActorRef currentLast = null;
		StateAndTime currentLastStateAndTime = null;
		for (ActorRef human : humans.keySet()) {
			if (currentLast == null) {
				currentLast = human;
				currentLastStateAndTime = humans.get(human);
			}
			StateAndTime stateAndTime = humans.get(human);
			if (stateAndTime.state == null || stateAndTime.state.equals(HumanState.INSIDE)) {
				if (stateAndTime.state != null && stateAndTime.state.equals(currentLastStateAndTime.state)) {
					if (stateAndTime.time.isAfter(currentLastStateAndTime.time)) {
						currentLast = human;
						currentLastStateAndTime = stateAndTime;
					}
				} else {
					currentLast = human;
					currentLastStateAndTime = stateAndTime;
				}
			}
		}
		return currentLast;
	}

	public static class StateReport {
		public final double light; // lx
		public final double temperature;
		public final double deltaTime;
		public final HashMap<ActorRef, HumanStateChanger.StateAndTime> housemates;
		public final boolean last;

		public StateReport(double light, double temperature, double deltaTime, HashMap<ActorRef, HumanStateChanger.StateAndTime> housemates, boolean last) {
			this.light = light;
			this.temperature = temperature;
			this.deltaTime = deltaTime;
			this.housemates = housemates;
			this.last = last;
		}
	}

	public static class RegisterForState {
	}

	public static class PowerUsageSignal {
		public final double powerUsed;

		public PowerUsageSignal(double powerUsed) {
			this.powerUsed = powerUsed;
		}
	}

	public static class LightSignal {
		public final double light;

		public LightSignal(double light) {
			this.light = light;
		}
	}

	public static class HeatSignal {
		public final double heat;
		public final boolean weather;

		public HeatSignal(double heat) {
			this(heat, false);
		}

		public HeatSignal(double heat, boolean isWeather) {
			this.heat = heat;
			this.weather = isWeather;

		}
	}
}
