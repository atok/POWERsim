package agh.powerSim.simulation.actors.devices;

import agh.powerSim.simulation.actors.ClockActor;
import akka.actor.ActorRef;

public class MicrowaveOven extends BaseDevice {

    public MicrowaveOven(ActorRef house) {
        super(house);
    }

    @Override
    protected void onTime(ClockActor.TimeSignal t) {

    }

    @Override
    public DeviceState getState() {
        return null;
    }
}
