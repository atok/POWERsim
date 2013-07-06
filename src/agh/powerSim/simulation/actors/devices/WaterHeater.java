package agh.powerSim.simulation.actors.devices;

import agh.powerSim.simulation.actors.ClockActor;
import agh.powerSim.simulation.actors.House;
import agh.powerSim.simulation.actors.utils.CalculateUtils;
import agh.powerSim.simulation.actors.utils.DataRecorder;
import akka.actor.ActorRef;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WaterHeater extends BaseDevice {

    private double powerUsage = 2400; // [Watt]
    private int waterHeatTime = 120; // [second]
    private int waterColdTime = 480; // [second]
    private LocalDateTime scheduledOffTime = null;
    private LocalDateTime scheduledOnTime = null;

    private boolean isOn = true;    // ON by default!

    public static boolean logOn = true;

    public WaterHeater(ActorRef house) {
        super(house);
    }

    public static List<DeviceType> getDeviceTypes() {
        ArrayList<DeviceType> deviceTypes = new ArrayList<>(1);
        deviceTypes.add(DeviceType.HEATING);
        return deviceTypes;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        super.onReceive(message);
    }

    protected void onTime(ClockActor.TimeSignal t) {
        double power = CalculateUtils.powerUsage(powerUsage, t.deltaTime);
        getHouse().tell(new House.PowerUsageSignal(power));

        if(scheduledOffTime == null && scheduledOnTime == null) {
            scheduledOffTime = t.time.plusSeconds(waterHeatTime);
            isOn = true;
            if(logOn){
                getContext().actorFor("akka://SimSystem/user/recorder").tell(new DataRecorder.StatusRecord("OnOff status " + Boolean.toString(isOn), t.time, getSelf()), getSelf());
            }
        }

        if(scheduledOffTime != null && t.time.isAfter(scheduledOffTime)) {
            scheduledOffTime = null;
            isOn = false;
            scheduledOnTime = t.time.plusSeconds(waterColdTime);
            if(logOn){
                getContext().actorFor("akka://SimSystem/user/recorder").tell(new DataRecorder.StatusRecord("OnOff status " + Boolean.toString(isOn), t.time, getSelf()), getSelf());
            }
        }

        if(scheduledOnTime != null && t.time.isAfter(scheduledOnTime)) {
            scheduledOnTime = null;
            isOn = true;
            scheduledOffTime = t.time.plusSeconds(waterHeatTime);
        }

        if(logOn){
            getContext().actorFor("akka://SimSystem/user/recorder").tell(new DataRecorder.PowerUsageRecord(power, 0, t.time, getSelf()), getSelf());
            log.warning("Water heater is " + (isOn ? "ON" : "OFF"));
        }
    }

    @Override
    public BaseDevice.DeviceState getState() {
        return new BaseDevice.DeviceState(isOn, isOn ? powerUsage : 0, isOn ? "ON" : "OFF", isOn ? "Device is ON" : "Device is OFF", getDeviceTypes());
    }


}
