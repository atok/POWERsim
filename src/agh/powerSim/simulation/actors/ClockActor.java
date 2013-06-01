package agh.powerSim.simulation.actors;

import agh.powerSim.simulation.actors.devices.BaseDevice;
import agh.powerSim.simulation.actors.humans.BaseHuman;
import akka.actor.ActorRef;
import akka.actor.DeadLetter;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.joda.time.LocalDateTime;
import scala.util.regexp.Base;

import java.io.Serializable;
import java.util.Date;
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
    

    public static enum TurnType {houses, devices, humans, others};
    private TurnType turn = null;

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
            registerActor(s);
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

        sendTimeSignal();
        return true;
    }

    private void sendTimeSignal() {
    	
    	if(now.isAfter(LocalDateTime.fromDateFields(new Date(1000*60*60*200)))){
    		System.exit(0);
    	}

        if(turn == null) {
            turn = TurnType.houses;
        } else if (turn == TurnType.houses) {
            turn = TurnType.devices;
        } else if (turn == TurnType.devices) {
            turn = TurnType.humans;
        } else if (turn == TurnType.humans) {
            turn = TurnType.others;
        } else if (turn == TurnType.others) {
            turn = TurnType.houses;
            now = now.plusSeconds(timeDelta);
            log.warning("Moving time to " + now);
        }

//        log.warning("turn: " + turn);

        boolean nothingToDo = true;

        // TODO 3 lists (SLOOOOW)
        for(Map.Entry<ActorRef, ActorItem> item: registeredActors.entrySet()) {

            if(turn == TurnType.houses) {
                if(House.class.isAssignableFrom(item.getValue().type)) {
                    sendTimeSignalTo(item.getValue());
                    nothingToDo = false;
                }
            }else if(turn == TurnType.devices) {
                if(BaseDevice.class.isAssignableFrom(item.getValue().type)) {
                    sendTimeSignalTo(item.getValue());
                    nothingToDo = false;
                }
            } else if(turn == TurnType.humans) {
                if(BaseHuman.class.isAssignableFrom(item.getValue().type)) {
                    sendTimeSignalTo(item.getValue());
                    nothingToDo = false;
                }
            } else if(turn == TurnType.others) {
                if(!BaseHuman.class.isAssignableFrom(item.getValue().type)
                        && !BaseDevice.class.isAssignableFrom(item.getValue().type)
                        && !House.class.isAssignableFrom(item.getValue().type) ) {
                    sendTimeSignalTo(item.getValue());
                    nothingToDo = false;
                }
            }
        }

        if(nothingToDo) {
            sendTimeSignal();
        }
    }

    private void sendTimeSignalTo(ActorItem actor) {
        actor.ready = false;
        actor.actor.tell(new TimeSignal(timeDelta, new LocalDateTime(now)), getSelf());
    }

    private void registerActor(RegisterActorSignal actor) {
        registeredActors.put(actor.actor, new ActorItem(actor.actor, actor.type));
        log.warning("Actor registered: " + actor);
    }

    private void markActorAsReady(ActorRef actor) {
        registeredActors.get(actor).ready = true;
        tryToSendTimeSignal();  // TODO optimization (no need to check every time)
    }

    private static class ActorItem {
        public ActorRef actor;
        public boolean ready;
        public final Class<?> type;
        private ActorItem(ActorRef actor, Class<?> type) {
            this.actor = actor;
            this.type = type;
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
        public final Class<?> type;
        public RegisterActorSignal(ActorRef rector, Class<?> type) {
            this.actor = rector;
            this.type = type;
        }
    }

    public static class TimeSignal implements Serializable {
        public final double deltaTime; //seconds
        public final LocalDateTime time;
        /**
         * @param timeDelta time step length in seconds
         * @param time actual calendar time
         */
        public TimeSignal(double timeDelta, LocalDateTime time) {
            this.deltaTime = timeDelta;
            this.time = time;
        }
        public String toString() {
            return time.toString() + " (d=" + deltaTime + ")";
        }
    }

    public static class DoneSignal {}
}
