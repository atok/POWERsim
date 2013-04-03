package agh.powerSim.simulation.actors.humans;

import agh.powerSim.simulation.actors.ClockActor;
import agh.powerSim.simulation.actors.House;
import agh.powerSim.simulation.actors.devices.Lamp;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.util.ArrayList;

public class Human extends BaseHuman {

    public Human(ActorRef house, ArrayList<DeviceToken> devices) {
        super(house, devices);
    }

    @Override
    public void preStart() {
        super.preStart();
        getContext().actorFor("akka://SimSystem/user/clock").tell(new ClockActor.RegisterActorSignal(), getSelf());
        getHouse().tell(new House.RegisterForState(), getSelf());
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
        //TODO
    }

    @Override
    protected void onHouseState(House.StateReport report) {
        if (report.light < 50) {
            log.warning("TO DARK!");
            for(DeviceToken device : getDevices()) {
                if(device.is(Lamp.class)) {
                    log.warning("turning lamp ON");
                    device.actor.tell(new Lamp.OnOffSignal(true));
                }
            }
        }
    }

    public static class SomeCustomEvent {}
}
