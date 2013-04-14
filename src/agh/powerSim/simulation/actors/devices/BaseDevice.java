package agh.powerSim.simulation.actors.devices;

import agh.powerSim.simulation.actors.ClockActor;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public abstract class BaseDevice extends UntypedActor {

    protected LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private final ActorRef house;
    public BaseDevice( ActorRef house) {
        this.house = house;
    }

    protected ActorRef getHouse() {
        return house;
    }

    @Override
    public void preStart() {
        super.preStart();
        getContext().actorFor("akka://SimSystem/user/clock").tell(new ClockActor.RegisterActorSignal(getSelf(), BaseDevice.class), getSelf());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof ClockActor.TimeSignal) {
            ClockActor.TimeSignal t = (ClockActor.TimeSignal)message;
            onTime(t);
            getSender().tell(new ClockActor.DoneSignal(), getSelf());
        } else if (message instanceof StateInfoRequest) {
            getSender().tell(getState(), getSelf());
        } else {
            unhandled(message);
        }
    }

    protected abstract void onTime(ClockActor.TimeSignal t);
    public abstract DeviceState getState();

    public static final class DeviceState {
        public final boolean isOn;
        public final double momentaryPowerDraw;
        public final String stateTitle;
        public final String stateDescription;
        public DeviceState(boolean on, double momentaryPowerDraw, String stateTitle, String stateDescription) {
            isOn = on;
            this.momentaryPowerDraw = momentaryPowerDraw;
            this.stateTitle = stateTitle;
            this.stateDescription = stateDescription;
        }
    }

    public static final class StateInfoRequest {}

}
