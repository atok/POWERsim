package agh.powerSim.simulation;

import agh.powerSim.simulation.actors.ClockActor;
import agh.powerSim.simulation.actors.DataRecorderActor;
import agh.powerSim.simulation.actors.House;
import agh.powerSim.simulation.actors.devices.BaseDevice;
import agh.powerSim.simulation.actors.environment.WeatherActor;
import agh.powerSim.simulation.actors.humans.Human;
import agh.powerSim.simulation.actors.humans.HumanCharacter;
import agh.powerSim.simulation.actors.humans.HumanStateChangeTime;
import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.joda.time.LocalDateTime;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;

public class Simulation {

	LoggingAdapter log;

	private final ActorSystem system;
	private final ActorRef clockActor;
	private final ActorRef dataRecorderActor;
	private final ActorRef weather;

	public Simulation() {
		system = ActorSystem.create("SimSystem");
		clockActor = system.actorOf(new Props(ClockActor.class), "clock");
		weather = system.actorOf(new Props(WeatherActor.class), "weather");
		dataRecorderActor = system.actorOf(new Props(DataRecorderActor.class), "recorder");
		log = Logging.getLogger(system, this);
	}

	public ActorRef addHuman(final Class<? extends Human> humanClass, String name, final ActorRef house, final ArrayList<Human.DeviceToken> devices) {
		return addHuman(humanClass, name, house, devices, new HumanCharacter());
	}

	public ActorRef addHuman(final Class<? extends Human> humanClass, String name, final ActorRef house, final ArrayList<Human.DeviceToken> devices, final HumanCharacter humanCharacter) {
		return addHuman(humanClass, name, house, devices, humanCharacter, null);
	}

	public ActorRef addHuman(final Class<? extends Human> humanClass, String name, final ActorRef house, final ArrayList<Human.DeviceToken> devices, final HumanCharacter humanCharacter, final HumanStateChangeTime[] stateChanges) {
		Props props = new Props(new UntypedActorFactory() {
			public UntypedActor create() {
				try {
					Constructor<? extends Human> constructor = humanClass.getDeclaredConstructor(ActorRef.class, ArrayList.class, HumanCharacter.class, HumanStateChangeTime[].class);
					return constructor.newInstance(house, devices, humanCharacter, stateChanges);
				} catch (NoSuchMethodException e) {
					throw new RuntimeException(e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e);
				} catch (InstantiationException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		});
		final ActorRef deviceActor = addActor(props, name);
		return deviceActor;
	}

	public ActorRef addDevice(final Class<? extends BaseDevice> deviceClass, String name, final ActorRef house, final Map<String, String> parameters) {
		Props props = new Props(new UntypedActorFactory() {
			public UntypedActor create() {
				try {
					Constructor<? extends BaseDevice> constructor = deviceClass.getDeclaredConstructor(ActorRef.class);
					UntypedActor instance = constructor.newInstance(house);
                    BaseDevice baseDevice = deviceClass.cast(instance);
                    baseDevice.init(parameters);
                    return baseDevice;
				} catch (NoSuchMethodException e) {
					throw new RuntimeException(e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e);
				} catch (InstantiationException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				} catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            }
		});
		final ActorRef deviceActor = addActor(props, name);
		return deviceActor;
	}

	public ActorRef addHouse(final Class<? extends House> deviceClass, String name) {
		Props props = new Props(new UntypedActorFactory() {
			public UntypedActor create() {
				try {
					Constructor<? extends House> constructor = deviceClass.getDeclaredConstructor();
					return constructor.newInstance();
				} catch (NoSuchMethodException e) {
					throw new RuntimeException(e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e);
				} catch (InstantiationException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		});
		final ActorRef deviceActor = addActor(props, name);
		weather.tell(new WeatherActor.NewHouse(deviceActor));
		return deviceActor;
	}

	public ActorRef addActor(Class<? extends Actor> actorClass, String name) {
		return system.actorOf(new Props(actorClass), name);
	}

	public ActorRef addActor(Props props, String name) {
		return system.actorOf(props, name);
	}

	public void start() {
		log.warning("Simulation start");
		clockActor.tell(new ClockActor.StartSimulation(30, new LocalDateTime(0)));
	}

	public void stop() {
		clockActor.tell(new ClockActor.StopSimulation());
	}

}
