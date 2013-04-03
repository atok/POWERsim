package agh.powerSim.simulation.actors.humans;

import agh.powerSim.simulation.actors.ClockActor;
import agh.powerSim.simulation.actors.House;
import agh.powerSim.simulation.actors.devices.Lamp;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.util.ArrayList;

public abstract class BaseHuman extends UntypedActor {

    protected LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private final ActorRef house;
    private final ArrayList<DeviceToken> devices;

    public BaseHuman(ActorRef house, ArrayList<DeviceToken> devices) {
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
            onTime(t);
            getSender().tell(new ClockActor.DoneSignal(), getSelf());
        } else if (message instanceof House.StateReport) {
            House.StateReport report = (House.StateReport)message;
            onHouseState(report);
        } else {
            unhandled(message);
        }
    }

    protected ActorRef getHouse() {
        return house;
    }

    protected ArrayList<DeviceToken> getDevices() {
        return devices;
    }

    protected abstract void onTime(ClockActor.TimeSignal timeSignal);
    protected abstract void onHouseState(House.StateReport report);

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
