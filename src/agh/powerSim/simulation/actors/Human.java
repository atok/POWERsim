package agh.powerSim.simulation.actors;

import agh.powerSim.simulation.actors.devices.Lamp;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.util.ArrayList;

public class Human extends UntypedActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private final ActorRef house;
    private final ArrayList<DeviceToken> devices;

    public Human(ActorRef house, ArrayList<DeviceToken> devices) {
        this.house = house;
        this.devices = devices;
    }

    @Override
    public void preStart() {
        super.preStart();
        getContext().actorFor("akka://SimSystem/user/clock").tell(new ClockActor.RegisterActorSignal(), getSelf());
        house.tell(new House.RegisterForState(), getSelf());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof ClockActor.TimeSignal) {
            ClockActor.TimeSignal t = (ClockActor.TimeSignal)message;
            reactToTime(t);
            // All time consuming work should be done here.
            getSender().tell(new ClockActor.DoneSignal(), getSelf());
        } else if (message instanceof House.StateReport) {
            // ...not here.
            House.StateReport report = (House.StateReport)message;
            reactToHouseState(report);
        } else {
            unhandled(message);
        }
    }

    private void reactToTime(ClockActor.TimeSignal timeSignal) {
        //TODO
    }

    private void reactToHouseState(House.StateReport report) {
        if (report.light < 50) {
            log.warning("TO DARK!");
            for(DeviceToken device : devices) {
                if(device.is(Lamp.class)) {
                    log.warning("turning lamp ON");
                    device.actor.tell(new Lamp.OnOffSignal(true));
                }
            }
        }
    }

    public static class DeviceToken {
        public final Class<?> type;
        public final ActorRef actor;
        public DeviceToken(Class<?> type, ActorRef actor) {
            this.type = type;
            this.actor = actor;
        }
        public boolean is(Class<?> type) {
            return this.type.isAssignableFrom(type);
        }
    }
}
