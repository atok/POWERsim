package agh.powerSim.simulation;

import agh.powerSim.simulation.actors.ClockActor;
import agh.powerSim.simulation.actors.devices.BaseDevice;
import agh.powerSim.simulation.actors.humans.Human;
import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.joda.time.LocalDateTime;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class Simulation {

    LoggingAdapter log;

    private final ActorSystem system;
    private final ActorRef clockActor;

    public Simulation() {
        system = ActorSystem.create("SimSystem");
        clockActor = system.actorOf(new Props(ClockActor.class), "clock");
        log = Logging.getLogger(system, this);
    }

    public ActorRef addHuman(final Class<? extends Human> humanClass, String name, final ActorRef house, final ArrayList<Human.DeviceToken> devices) {
        Props props = new Props(new UntypedActorFactory() {
            public UntypedActor create() {
                try {
                    Constructor<? extends Human> constructor = humanClass.getDeclaredConstructor(ActorRef.class, ArrayList.class);
                    return constructor.newInstance(house, devices);
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

    public ActorRef addDevice(final Class<? extends BaseDevice> deviceClass, String name, final ActorRef house) {
        Props props = new Props(new UntypedActorFactory() {
            public UntypedActor create() {
                try {
                    Constructor<? extends BaseDevice> constructor = deviceClass.getDeclaredConstructor(ActorRef.class);
                    return constructor.newInstance(house);
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

    public ActorRef addActor(Class<? extends Actor> actorClass, String name) {
        return system.actorOf(new Props(actorClass), name);
    }

    public ActorRef addActor(Props props, String name) {
        return system.actorOf(props, name);
    }

    public void start() {
        log.warning("Simulation start");
        clockActor.tell(new ClockActor.StartSimulation(10, new LocalDateTime(0)));
    }

    public void stop() {
        clockActor.tell(new ClockActor.StopSimulation());
    }

}
