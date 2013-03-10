package agh.powerSim.simulation.actors;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.util.Random;

public class ExampleActor extends UntypedActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    @Override
    public void preStart() {
        super.preStart();
        getContext().actorFor("akka://SimSystem/user/clock").tell(new ClockActor.RegisterActorSignal(), getSelf());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof ClockActor.TimeSignal) {
            ClockActor.TimeSignal t = (ClockActor.TimeSignal)message;
            log.warning("Received time event: " + t);

            // #########################################################
            // #  DO NOT DO THIS!!!!!                                  #
            // #  Actor should not affect the thread its using.        #
            // #  Be a good citizen and don't exhaust the thread pool  #
            // #  PS. for time dependent things use TimeSignals        #
            // #########################################################
            int timeToSleep = 1000 + (new Random()).nextInt(3000);
            log.warning("Sleeping: " + timeToSleep);
            Thread.sleep(timeToSleep);

            getSender().tell(new ClockActor.DoneSignal(), getSelf());
        } else {
            unhandled(message);
        }
    }
}