package agh.powerSim.simulation;

import agh.powerSim.simulation.actors.ClockActor;
import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.joda.time.LocalDateTime;

public class Simulation {

    LoggingAdapter log;

    private final ActorSystem system;
    private final ActorRef clockActor;

    public Simulation() {
        system = ActorSystem.create("SimSystem");
        clockActor = system.actorOf(new Props(ClockActor.class), "clock");
        log = Logging.getLogger(system, this);
    }

    public void addActor(Class<? extends Actor> actorClass, String name) {
        system.actorOf(new Props(actorClass), name);
    }

    public void addActor(Props props, String name, Class<? extends Actor> levelClass) {
        system.actorOf(props, name);
    }

    public void start() {
        log.warning("Simulation start");
        clockActor.tell(new ClockActor.StartSimulation(10, new LocalDateTime(0)));
    }

    public void stop() {
        clockActor.tell(new ClockActor.StopSimulation());
    }


}
