package agh.powerSim.simulation.actors.devices;

import agh.powerSim.simulation.actors.ClockActor;
import agh.powerSim.simulation.actors.House;
import agh.powerSim.simulation.actors.utils.CalculateUtils;
import agh.powerSim.simulation.actors.utils.DataRecorder;
import akka.actor.ActorRef;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDateTime;

public class ElectricHeater extends BaseDevice {

    private final double powerUsage = 1000;
    private final double heatGen = 10;
    private boolean isOn = false;

	public static boolean logOn = true;
	
    public ElectricHeater(ActorRef house) {
        super(house);
    }

    public static List<DeviceType> getDeviceTypes() {
        ArrayList<DeviceType> deviceTypes = new ArrayList<DeviceType>(1);
        deviceTypes.add(DeviceType.HEATING);
        return deviceTypes;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof OnOffSignal) {
            OnOffSignal m = (OnOffSignal)message;
            isOn = m.state;
            //log.warning("state := " + isOn);
			if(logOn){
				getContext().actorFor("akka://SimSystem/user/recorder").tell(new DataRecorder.StatusRecord("OnOff status"+Boolean.toString(isOn), m.time, getSelf()), getSelf());
			}
        } else {
            super.onReceive(message);
        }
    }

    protected void onTime(ClockActor.TimeSignal t) {
        if(isOn) {
            double power = CalculateUtils.powerUsage(powerUsage, t.deltaTime);
            getHouse().tell(new House.PowerUsageSignal(power), getSelf());

            double heat = heatGen * t.deltaTime;
            getHouse().tell(new House.HeatSignal(heat), getSelf());
            

			if(logOn){
				getContext().actorFor("akka://SimSystem/user/recorder").tell(new DataRecorder.PowerUsageRecord(power, 0, t.time, getSelf()), getSelf());

				log.warning("Lamp is " + (isOn ? "ON" : "OFF"));
			}
        }
    }

    @Override
    public DeviceState getState() {
        return new DeviceState(isOn, isOn ? powerUsage : 0, isOn ? "ON": "OFF", isOn ? "Device is ON": "Device is OFF", getDeviceTypes());
    }

    public static class OnOffSignal {
        public final boolean state;
        public final LocalDateTime time;

        public OnOffSignal(boolean state, LocalDateTime time) {
            this.state = state;
            this.time = time;
        }
    }

}
