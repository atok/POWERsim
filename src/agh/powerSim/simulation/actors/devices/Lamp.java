package agh.powerSim.simulation.actors.devices;

import agh.powerSim.simulation.actors.ClockActor;
import agh.powerSim.simulation.actors.House;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Lamp extends Device {

    private final double powerUsage = 10;
    private final double lightGen = 10;
    private boolean isOn = false;

    private final ActorRef house;
    public Lamp( ActorRef house) {
        this.house = house;
    }



    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof ClockActor.TimeSignal) {
            ClockActor.TimeSignal t = (ClockActor.TimeSignal)message;
            onTime(t);
            getSender().tell(new ClockActor.DoneSignal(), getSelf());
        } else if(message instanceof OnOffSignal) {
            OnOffSignal m = (OnOffSignal)message;
            isOn = m.state;

            log.warning("state = " + isOn);
        } else {
            unhandled(message);
        }
    }

    protected void onTime(ClockActor.TimeSignal t) {
        if(isOn) {
            double power = powerUsage * t.timeDelta;
            house.tell(new House.PowerUsageSignal(power));

            double light = lightGen * t.timeDelta;
            house.tell(new House.LightSignal(light));
        }
    }

    public static class OnOffSignal {
        public final boolean state;

        public OnOffSignal(boolean state) {
            this.state = state;
        }
    }


}
