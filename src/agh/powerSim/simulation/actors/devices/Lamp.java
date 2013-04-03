package agh.powerSim.simulation.actors.devices;

import agh.powerSim.simulation.actors.ClockActor;
import agh.powerSim.simulation.actors.House;
import akka.actor.ActorRef;

public class Lamp extends BaseDevice {

    private final double powerUsage = 10;
    private final double lightGen = 10;
    private boolean isOn = false;

    public Lamp(ActorRef house) {
        super(house);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof OnOffSignal) {
            OnOffSignal m = (OnOffSignal)message;
            isOn = m.state;
            log.warning("state := " + isOn);
        } else {
            super.onReceive(message);
        }
    }

    protected void onTime(ClockActor.TimeSignal t) {
        if(isOn) {
            double power = powerUsage * t.timeDelta;
            getHouse().tell(new House.PowerUsageSignal(power));

            double light = lightGen * t.timeDelta;
            getHouse().tell(new House.LightSignal(light));
        }
    }

    public static class OnOffSignal {
        public final boolean state;

        public OnOffSignal(boolean state) {
            this.state = state;
        }
    }


}
