package agh.powerSim.simulation.actors.devices;

import agh.powerSim.simulation.actors.ClockActor;
import agh.powerSim.simulation.actors.House;
import akka.actor.ActorRef;

import java.util.ArrayList;
import java.util.List;

public class Lamp extends BaseDevice{

    private final double powerUsage = 10;
    private final double lightGen = 10;
    private boolean isOn = false;

    public Lamp(ActorRef house) {
        super(house);
    }

    public static List<DeviceType> getDeviceTypes() {
        ArrayList<DeviceType> deviceTypes = new ArrayList<DeviceType>(1);
        deviceTypes.add(DeviceType.LIGHT);
        return deviceTypes;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof OnOffSignal) {
            OnOffSignal m = (OnOffSignal)message;
            isOn = m.state;
            //log.warning("state := " + isOn);
        } else {
            super.onReceive(message);
        }
    }

    protected void onTime(ClockActor.TimeSignal t) {
        if(isOn) {
            double power = powerUsage * t.deltaTime;
            getHouse().tell(new House.PowerUsageSignal(power));

            double light = lightGen * t.deltaTime;
            getHouse().tell(new House.LightSignal(light));
            
            log.warning("Lamp is "+(isOn?"ON":"OFF"));
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
