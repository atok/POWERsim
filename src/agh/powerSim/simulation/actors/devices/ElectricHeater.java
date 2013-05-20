package agh.powerSim.simulation.actors.devices;

import agh.powerSim.simulation.actors.ClockActor;
import agh.powerSim.simulation.actors.House;
import akka.actor.ActorRef;

import java.util.ArrayList;
import java.util.List;

public class ElectricHeater extends BaseDevice {

    private final double powerUsage = 100;
    private final double heatGen = 10;
    private boolean isOn = false;

    public ElectricHeater(ActorRef house) {
        super(house);
    }

    public static List<DeviceType> getDeviceTypes() {
        ArrayList<DeviceType> deviceTypes = new ArrayList<DeviceType>(1);
        deviceTypes.add(DeviceType.LAMP);
        return deviceTypes;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof OnOffSignal) {
            OnOffSignal m = (OnOffSignal)message;
            isOn = m.state;
            log.warning("state := " + isOn);
        } else {
            super.onReceive(message);
        }
    }

    protected void onTime(ClockActor.TimeSignal t) {
        if(isOn) {
            double power = powerUsage * t.timeDelta;
            getHouse().tell(new House.PowerUsageSignal(power));

            double heat = heatGen * t.timeDelta;
            getHouse().tell(new House.HeatSignal(heat));
        }
    }

    @Override
    public DeviceState getState() {
        return new DeviceState(isOn, isOn ? powerUsage : 0, isOn ? "ON": "OFF", isOn ? "Device is ON": "Device is OFF", getDeviceTypes());
    }

    public static class OnOffSignal {
        public final boolean state;

        public OnOffSignal(boolean state) {
            this.state = state;
        }
    }

}
