package agh.powerSim.simulation.actors.utils;

import agh.powerSim.simulation.actors.ClockActor;
import agh.powerSim.simulation.actors.DataRecorderActor;
import akka.actor.ActorRef;

public class DataRecorder {

    public DataRecorder() {
    }

    public void store(DataRecorderActor.DataReport report) {
        if(report instanceof PowerUsageRecord) {
            PowerUsageRecord r = (PowerUsageRecord)report;

            //TODO data storage
        }
    }


    public static class PowerUsageRecord extends DataRecorderActor.DataReport {
        final double powerUsedInThisStep;
        final double lightProvidedInThisStep;

        public PowerUsageRecord(double powerUsedInThisStep, double lightProvidedInThisStep, ClockActor.TimeSignal time, ActorRef sender) {
            super(time, sender);
            this.powerUsedInThisStep = powerUsedInThisStep;
            this.lightProvidedInThisStep = lightProvidedInThisStep;
        }
    }
}
