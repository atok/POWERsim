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

public class MicrowaveOven extends BaseDevice {

    private double powerUsage = 1100; // [Watt]
    private int cookTimeBase = 30; // [second]
    private int cookBoilTimeBonus = 180;
    private LocalDateTime scheduledOfTime = null;

    private boolean isOn = false;

    public static boolean logOn = true;

    public MicrowaveOven(ActorRef house) {
        super(house);
    }

    public static List<DeviceType> getDeviceTypes() {
        ArrayList<DeviceType> deviceTypes = new ArrayList<>(1);
        deviceTypes.add(DeviceType.MEAL);
        return deviceTypes;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof BaseDevice.OnOffSignal) {
            BaseDevice.OnOffSignal m = (BaseDevice.OnOffSignal) message;
            if(!isOn) {
                isOn = m.state;
                scheduledOfTime = m.time.plusSeconds(cookTimeBase + (new Random()).nextInt(cookBoilTimeBonus));
            }

            if(logOn){
                getContext().actorFor("akka://SimSystem/user/recorder").tell(new DataRecorder.StatusRecord("OnOff status"+Boolean.toString(isOn), m.time, getSelf()), getSelf());
            }
        } else {
            super.onReceive(message);
        }
    }

    protected void onTime(ClockActor.TimeSignal t) {
        if (isOn) {
            double power = CalculateUtils.powerUsage(powerUsage, t.deltaTime);
            getHouse().tell(new House.PowerUsageSignal(power));

            if(t.time.isAfter(scheduledOfTime)) {
                isOn = false;
            }

            if(logOn){
                getContext().actorFor("akka://SimSystem/user/recorder").tell(new DataRecorder.PowerUsageRecord(power, 0, t.time, getSelf()), getSelf());

                log.warning("Kettle is " + (isOn ? "ON" : "OFF"));
            }
        }
    }

    @Override
    public BaseDevice.DeviceState getState() {
        return new BaseDevice.DeviceState(isOn, isOn ? powerUsage : 0, isOn ? "ON" : "OFF", isOn ? "Device is ON" : "Device is OFF", getDeviceTypes());
    }
}
