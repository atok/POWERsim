package agh.powerSim.simulation.actors.humans;

import agh.powerSim.simulation.actors.ClockActor;
import agh.powerSim.simulation.actors.House;
import agh.powerSim.simulation.actors.devices.Lamp;
import akka.actor.ActorRef;

import java.util.ArrayList;

public class Human extends BaseHuman {

    public static enum State {Sleeping, Awake};
    private State state = State.Awake;

    ClockActor.TimeSignal currentTime;

    public Human(ActorRef house, ArrayList<DeviceToken> devices) {
        super(house, devices);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof SomeCustomEvent) {
            //EXAMPLE
        } else {
            super.onReceive(message);
        }
    }

    @Override
    protected void onTime(ClockActor.TimeSignal timeSignal) {
        currentTime = timeSignal;
        requestDevicesStateUpdate();
    }

    @Override
    protected void onHouseState(House.StateReport report) {
        lightActions(report.light);
    }

    private void requestMoreLight() {
        for(DeviceTokenWithState device : getDevices()) {
            if(device.is(Lamp.class) && device.state.isOn == false && device.stateChangeRequested == false) {
                device.actor.tell(new Lamp.OnOffSignal(true));
                break;
            }
        }
    }

    private void lightActions(double lightLevel) {
        log.warning("lightLevel " + lightLevel);
        if(state == State.Awake) {
            if(lightLevel < 300) {
                log.warning("More light!!!");
                requestMoreLight();
            }
        } else if(state == State.Sleeping) {

        }
    }

    public static class SomeCustomEvent {}
}
