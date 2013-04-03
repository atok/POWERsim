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

            getSender().tell(new ClockActor.DoneSignal(), getSelf());
        } else if (message instanceof House.StateReport) {
            House.StateReport report = (House.StateReport)message;
            if (report.light < 50) {

                log.warning("TO DARK!");

                for(DeviceToken device : devices) {
                    if(device.type.isAssignableFrom(Lamp.class)) {

                        log.warning("turning lamp ON");
                        device.actor.tell(new Lamp.OnOffSignal(true));
                    }
                }


            }
        } else {
            unhandled(message);
        }
    }

    public static class DeviceToken {
        public final Class<?> type;
        public final ActorRef actor;
        public DeviceToken(Class<?> type, ActorRef actor) {
            this.type = type;
            this.actor = actor;
        }
    }
}
