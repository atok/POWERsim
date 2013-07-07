package agh.powerSim.simulation.actors.devices;

import agh.powerSim.simulation.actors.ClockActor;
import agh.powerSim.simulation.actors.House;
import agh.powerSim.simulation.actors.utils.CalculateUtils;
import agh.powerSim.simulation.actors.utils.DataRecorder;
import akka.actor.ActorRef;

import java.util.ArrayList;
import java.util.List;

public class TelevisionSet extends BaseDevice {

    private double powerUsage = 1000; // [Watt]
    private boolean isOn = false;

    public static boolean logOn = true;

    public TelevisionSet(ActorRef house) {
        super(house);
    }

    public static List<DeviceType> getDeviceTypes() {
        ArrayList<DeviceType> deviceTypes = new ArrayList<>(1);
        deviceTypes.add(DeviceType.ENTERTAINMENT);
        return deviceTypes;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof OnOffSignal) {
            OnOffSignal m = (OnOffSignal)message;
            isOn = m.state;
            log.warning("state := " + isOn);
            if(logOn){
                getContext().actorFor("akka://SimSystem/user/recorder").tell(new DataRecorder.StatusRecord("OnOff status"+Boolean.toString(isOn), m.time, getSelf()), getSelf());
            }
        } else {
            super.onReceive(message);
        }
    }

    @Override
    protected void onTime(ClockActor.TimeSignal t) {
        if (isOn) {
            double power = CalculateUtils.powerUsage(powerUsage, t.deltaTime);
            getHouse().tell(new House.PowerUsageSignal(power));

            if(logOn){
                getContext().actorFor("akka://SimSystem/user/recorder").tell(new DataRecorder.PowerUsageRecord(power, 0, t.time, getSelf()), getSelf());
                log.warning("TV is " + (isOn ? "ON" : "OFF"));
            }
        }
    }

    @Override
    public DeviceState getState() {
        return new BaseDevice.DeviceState(isOn, isOn ? powerUsage : 0, isOn ? "ON" : "OFF", isOn ? "Device is ON" : "Device is OFF", getDeviceTypes());
    }
}
