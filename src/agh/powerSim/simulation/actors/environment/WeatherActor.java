package agh.powerSim.simulation.actors.environment;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.joda.time.Interval;
import org.joda.time.LocalDateTime;

import agh.powerSim.gui.Context;
import agh.powerSim.simulation.actors.ClockActor;
import agh.powerSim.simulation.actors.House;
import agh.powerSim.simulation.actors.devices.BaseDevice;
import agh.powerSim.simulation.actors.utils.DataRecorder;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

public class WeatherActor extends UntypedActor {

	protected LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	protected String properties = "weather.properties";

	protected final List<ActorRef> houses = new LinkedList<>();

	public WeatherElement<Double> sun;

	public WeatherElement<Double> clouds;

	public WeatherElement<Double> temperature;

	public WeatherElement<Boolean> front;

	public static boolean logOn = true;

	private int viewUpdate = 0;

	@Override
	public void preStart() {
		sun = new Sun(properties);
		front = new Front();
		clouds = new Clouds();
		clouds.setRelatedElement(front);
		temperature = new Temperature(properties);
		temperature.setRelatedElement(front);
		temperature.setRelatedElement(clouds);
		super.preStart();
		getContext().actorFor("akka://SimSystem/user/clock").tell(new ClockActor.RegisterActorSignal(getSelf(), BaseDevice.class), getSelf());
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof NewHouse) {
			houses.add(((NewHouse) message).house);
			if (logOn)
				log.warning("New house added to the WORLD!");
		} else if (message instanceof ClockActor.TimeSignal) {
			ClockActor.TimeSignal t = (ClockActor.TimeSignal) message;
			sun.processTime(t.time, t.deltaTime);
			front.processTime(t.time, t.deltaTime);
			clouds.processTime(t.time, t.deltaTime);
			temperature.processTime(t.time, t.deltaTime);
			if (logOn) {
				getContext().actorFor("akka://SimSystem/user/recorder")
						.tell(new DataRecorder.StatusRecord("Wheather status: SUN=" + sun.getValue() + "; CLOUDS=" + clouds.getValue() + "; TEMP=" + temperature.getValue(), t.time, getSelf()), getSelf());
			}
			double lightProvided = 10 * (50.0 * sun.getValue() + 50.0 * (sun.getValue() * clouds.getValue()) / (sun.getValue() + clouds.getValue() + 1.0)) / 100.0 * t.deltaTime;
			double heatProvided = temperature.getValue() * t.deltaTime;
			// log.warning(temperature.getForecast());
			for (ActorRef house : houses) {
				house.tell(new House.LightSignal(lightProvided), getSelf());
				house.tell(new House.HeatSignal(heatProvided, true), getSelf());
			}
			Context.setWeather((sun.getValue() > 80.0 ? "SUN UP" : "SUN DOWN"), temperature.getValue() + " C", clouds.getValue() + "%");

			getSender().tell(new ClockActor.DoneSignal(), getSelf());
		} else {
			unhandled(message);
		}
	}

	public static class NewHouse {
		public ActorRef house;

		public NewHouse(ActorRef house) {
			this.house = house;
		}
	}

}
