package agh.powerSim.simulation.actors;

import org.joda.time.LocalDateTime;

import agh.powerSim.simulation.actors.utils.DataRecorder;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class DataRecorderActor extends UntypedActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    DataRecorder recorder;

    public DataRecorderActor() {
        recorder = new DataRecorder();
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof DataReport) {
            DataReport report = (DataReport)message;
            recorder.store(report);
        } else {
            unhandled(message);
        }
    }

    public static class DataReport {
        public LocalDateTime time;
        public ActorRef sender;

        public DataReport(LocalDateTime time, ActorRef sender) {
            this.time = time;
            this.sender = sender;
        }
    }
}
