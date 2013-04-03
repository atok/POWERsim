package agh.powerSim.simulation.actors;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.util.TreeSet;

public class House extends UntypedActor {

    TreeSet<ActorRef> humans = new TreeSet<ActorRef>();

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    double powerUsedInThisStep = 0;
    double lightProvidedInThisStep = 0;

    @Override
    public void preStart() {
        super.preStart();
        getContext().actorFor("akka://SimSystem/user/clock").tell(new ClockActor.RegisterActorSignal(), getSelf());
        log.error("house started");
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof ClockActor.TimeSignal) {
            ClockActor.TimeSignal t = (ClockActor.TimeSignal)message;

            log.error("Power usage: " + powerUsedInThisStep);
            log.error("Light provided: " + lightProvidedInThisStep);

            for(ActorRef human : humans) {
                human.tell(new StateReport(lightProvidedInThisStep));
            }

            powerUsedInThisStep = 0;
            lightProvidedInThisStep = 0;

            getSender().tell(new ClockActor.DoneSignal(), getSelf());

        } else if (message instanceof PowerUsageSignal) {
            PowerUsageSignal powerSignal = (PowerUsageSignal)message;
            powerUsedInThisStep += powerSignal.powerUsed;
        } else if (message instanceof LightSignal) {
            LightSignal lightSignal = (LightSignal)message;
            lightProvidedInThisStep += lightSignal.light;
        } else if (message instanceof RegisterForState) {
            humans.add(getSender());
        } else {
            unhandled(message);
        }
    }

    public static class StateReport {
        public final double light;
        public StateReport(double light) {
            this.light = light;
        }
    }

    public static class RegisterForState {}

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
}
