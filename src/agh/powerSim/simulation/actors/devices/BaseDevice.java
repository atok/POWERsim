package agh.powerSim.simulation.actors.devices;

import agh.powerSim.simulation.actors.ClockActor;
import agh.powerSim.simulation.actors.utils.ConversionHelper;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.joda.time.LocalDateTime;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class BaseDevice extends UntypedActor {

	public static boolean logOn = true;
	
    protected LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private final ActorRef house;

    
    public BaseDevice( ActorRef house) {
        this.house = house;
    }

    public void init(Map<String, String> parameters) throws IllegalAccessException, NoSuchFieldException {
        Set<String> fields = parameters.keySet();
        for(String fieldName : fields) {
                Field field = getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = ConversionHelper.convert((String) parameters.get(fieldName), field.getType());
                field.set(this, value);
        }
    }

//    public abstract List<DeviceType> getDeviceTypes();
    public static List<DeviceType> getDeviceTypes() {
        ArrayList<DeviceType> deviceTypes = new ArrayList<DeviceType>(1);
        return deviceTypes;
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
        public final List<DeviceType> deviceTypes;
        public DeviceState(boolean on, double momentaryPowerDraw, String stateTitle, String stateDescription, List<DeviceType> deviceTypes) {
            isOn = on;
            this.momentaryPowerDraw = momentaryPowerDraw;
            this.stateTitle = stateTitle;
            this.stateDescription = stateDescription;
            this.deviceTypes = deviceTypes;
        }
    }

    public static class OnOffSignal {
        public final boolean state;
        public final LocalDateTime time;

        public OnOffSignal(boolean state, LocalDateTime time) {
            this.state = state;
            this.time = time;
        }
    }

    public static final class StateInfoRequest {}

}
