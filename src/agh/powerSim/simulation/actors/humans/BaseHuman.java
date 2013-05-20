package agh.powerSim.simulation.actors.humans;

import agh.powerSim.simulation.actors.ClockActor;
import agh.powerSim.simulation.actors.House;
import agh.powerSim.simulation.actors.devices.BaseDevice;
import agh.powerSim.simulation.actors.devices.DeviceType;
import agh.powerSim.simulation.actors.devices.Lamp;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseHuman extends UntypedActor {

    protected LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private final ActorRef house;
    private final ArrayList<DeviceTokenWithState> devices;

    protected HumanCharacter humanCharacter;

    public BaseHuman(ActorRef house, ArrayList<DeviceToken> devices) {
        this.house = house;

        this.devices = new ArrayList<DeviceTokenWithState>();
        for(DeviceToken dt : devices) {
            this.devices.add(new DeviceTokenWithState(dt));
        }
    }

    public BaseHuman(ActorRef house, ArrayList<DeviceToken> devices, HumanCharacter humanCharacter) {
        this(house, devices);
        this.humanCharacter = humanCharacter;
    }

    @Override
    public void preStart() {
        super.preStart();
        getContext().actorFor("akka://SimSystem/user/clock").tell(new ClockActor.RegisterActorSignal(getSelf(), BaseHuman.class), getSelf());
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
        } else if (message instanceof BaseDevice.DeviceState) {
            updateDeviceState((BaseDevice.DeviceState)message, getSender());
        } else {
            unhandled(message);
        }
    }

    private void updateDeviceState(BaseDevice.DeviceState newState, ActorRef actor) {
        for(DeviceTokenWithState device : devices) {
            if(device.actor.equals(actor)) {
                device.state = newState;
                device.stateChangeRequested = false;
                break;
            }
        }
    }

    protected void requestDevicesStateUpdate() {
        for(DeviceToken device : getDevices()) {
            device.actor.tell(new BaseDevice.StateInfoRequest(), getSelf());
        }
    }

    protected ActorRef getHouse() {
        return house;
    }

    protected ArrayList<DeviceTokenWithState> getDevices() {
        return devices;
    }

    protected abstract void onTime(ClockActor.TimeSignal timeSignal);
    protected abstract void onHouseState(House.StateReport report);

    public static class DeviceTokenWithState extends DeviceToken {
        public BaseDevice.DeviceState state = new BaseDevice.DeviceState(false, 0, "", "", new ArrayList<DeviceType>());
        public boolean stateChangeRequested = false;

        public DeviceTokenWithState(DeviceToken token) {
            super(token.type, token.actor);
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

        /**
         * Checks whether device related to this token has selected type
         *
         * @param deviceType
         * @return
         */
        public boolean is(DeviceType deviceType) {
            return getTypesOfDevice().contains(deviceType);
        }

        /**
         * Fetch types configured for this device class
         *
         * @return
         */
        public List<DeviceType> getTypesOfDevice() {
            List<DeviceType> deviceTypes;

            try {
                Class<?> deviceClass = type;
                Method method = deviceClass.getDeclaredMethod("getDeviceTypes");
                deviceTypes = (List<DeviceType>) method.invoke(null);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                deviceTypes = new ArrayList<DeviceType>(1);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                deviceTypes = new ArrayList<DeviceType>(1);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                deviceTypes = new ArrayList<DeviceType>(1);
            } catch (Exception e) {
                e.printStackTrace();
                deviceTypes = new ArrayList<DeviceType>(1);
            }

            return deviceTypes;
        }
    }
}
