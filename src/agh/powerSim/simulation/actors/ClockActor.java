package agh.powerSim.simulation.actors;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.DeadLetter;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.joda.time.LocalDateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 Actor responsible of sending TimeSignals to every other time dependant actor in the system.
 To register for time events use
 */
public class ClockActor extends UntypedActor {

    HashMap<ActorRef, ActorItem> registeredActors = new HashMap<ActorRef, ActorItem>();
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    LocalDateTime now = null;
    int timeDelta = 0; //seconds

    @Override
    public void preStart() {
        super.preStart();
        getContext().system().eventStream().subscribe(getSelf(), DeadLetter.class);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof StartSimulation) {            //Start simulation with a given deltaTime and startDate
            StartSimulation time = (StartSimulation)message;
            timeDelta = time.timeDelta;
            now = time.time;
            log.warning("Time has started from " + time.time + " with delta = " + timeDelta + " seconds");
            tryToSendTimeSignal();
        } else if (message instanceof  RegisterActorSignal) {
            RegisterActorSignal s = (RegisterActorSignal)message;
            ActorRef actor = s.actor != null ? s.actor : getSender();
            registerActor(actor);
        } else if (message instanceof DoneSignal) {
            markActorAsReady(getSender());
        } else if (message instanceof DeadLetter) {
            log.error("!!!!" + message.toString());
        } else {
            unhandled(message);
        }
    }

    private boolean tryToSendTimeSignal() {
        boolean allReady = true;
        for(Map.Entry<ActorRef, ActorItem> item: registeredActors.entrySet()) {
            if (!item.getValue().ready){
                allReady = false;
                break;
            }
        }
        if(!allReady)
            return false;

        now = now.plusSeconds(timeDelta);
        log.warning("Moving time to " + now);

        for(Map.Entry<ActorRef, ActorItem> item: registeredActors.entrySet()) {
            ActorRef actor = item.getKey();
            item.getValue().ready = false;
            actor.tell(new TimeSignal(timeDelta, new LocalDateTime(now)), getSelf());
        }

        return true;
    }

    private void registerActor(ActorRef actor) {
        registeredActors.put(actor, new ActorItem(actor));
        log.warning("Actor registered: " + actor);
    }

    private void markActorAsReady(ActorRef actor) {
        registeredActors.get(actor).ready = true;
//        log.warning("Actor done: " + getSender());
        tryToSendTimeSignal();  // TODO optimization (no need to check every time)
    }

    private static class ActorItem {
        public ActorRef actor;
        public boolean ready;
        private ActorItem(ActorRef actor) {
            this.actor = actor;
            ready = true;
        }
    }

    //  SIGNALS

    public static class StartSimulation {
        final int timeDelta; //seconds
        final LocalDateTime time;
        /**
         * @param timeDelta time step length in seconds
         * @param time actual calendar time
         */
        public StartSimulation(int timeDelta, LocalDateTime time) {
            this.timeDelta = timeDelta;
            this.time = time;
        }
    }

    public static class StopSimulation {}

    public static class RegisterActorSignal {
        public final ActorRef actor;
        public RegisterActorSignal() {
            actor = null;
        }
        public RegisterActorSignal(ActorRef rector) {
            this.actor = rector;
        }
    }

    public static class TimeSignal implements Serializable {
        public final double timeDelta; //seconds
        public final LocalDateTime time;
        /**
         * @param timeDelta time step length in seconds
         * @param time actual calendar time
         */
        public TimeSignal(double timeDelta, LocalDateTime time) {
            this.timeDelta = timeDelta;
            this.time = time;
        }
        public String toString() {
            return time.toString() + " (d=" + timeDelta + ")";
        }
    }

    public static class DoneSignal {}
}
