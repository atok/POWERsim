package agh.powerSim.simulation.actors.devices;

import agh.powerSim.simulation.actors.ClockActor;
import agh.powerSim.simulation.actors.House;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Lamp extends UntypedActor {

    private final double powerUsage = 10;
    private final double lightGen = 10;
    private boolean isOn = false;

    private final ActorRef house;
    public Lamp( ActorRef house) {
        this.house = house;
    }

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

            if(isOn) {
                double power = powerUsage * t.timeDelta;
                house.tell(new House.PowerUsageSignal(power));

                double light = lightGen * t.timeDelta;
                house.tell(new House.LightSignal(light));
            }

            getSender().tell(new ClockActor.DoneSignal(), getSelf());
        } else if(message instanceof OnOffSignal) {
            OnOffSignal m = (OnOffSignal)message;
            isOn = m.state;

            log.warning("state = " + isOn);
        } else {
            unhandled(message);
        }
    }

    public static class OnOffSignal {
        public final boolean state;

        public OnOffSignal(boolean state) {
            this.state = state;
        }
    }


}
