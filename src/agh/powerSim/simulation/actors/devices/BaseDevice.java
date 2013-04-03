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
        getContext().actorFor("akka://SimSystem/user/clock").tell(new ClockActor.RegisterActorSignal(), getSelf());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof ClockActor.TimeSignal) {
            ClockActor.TimeSignal t = (ClockActor.TimeSignal)message;
            onTime(t);
            getSender().tell(new ClockActor.DoneSignal(), getSelf());
        } else {
            unhandled(message);
        }
    }

    protected abstract void onTime(ClockActor.TimeSignal t);

}
