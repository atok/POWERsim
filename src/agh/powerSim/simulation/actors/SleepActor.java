package agh.powerSim.simulation.actors;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.util.Random;

public class SleepActor extends UntypedActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private final int timeToSleep = 1000;

    @Override
    public void preStart() {
        super.preStart();
        getContext().actorFor("akka://SimSystem/user/clock").tell(new ClockActor.RegisterActorSignal(getSelf(), SleepActor.class), getSelf());
        log.warning("SleepActor ENABLED (t = " + timeToSleep + " ms)");
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof ClockActor.TimeSignal) {
            ClockActor.TimeSignal t = (ClockActor.TimeSignal)message;
//            log.warning("Received time event: " + t);

            // #########################################################
            // #  DO NOT DO THIS!!!!!                                  #
            // #  Actor should not affect the thread its using.        #
            // #  Be a good citizen and don't exhaust the thread pool  #
            // #  PS. for time dependent things use TimeSignals        #
            // #########################################################
            Thread.sleep(timeToSleep);
            getSender().tell(new ClockActor.DoneSignal(), getSelf());
        } else {
            unhandled(message);
        }
    }
}