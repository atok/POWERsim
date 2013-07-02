package agh.powerSim.simulation.db_model;

/**
 * Created with IntelliJ IDEA.
 * User: Karol
 * Date: 10.06.13
 * Time: 23:52
 */
public class SimulationLog {
    private int id; // integer
    private long simulationId; // long
    private String actorName; // text
    private String actorType; // text
    private String submitDate; // text: ISO8601 string ("YYYY-MM-DD HH:MM:SS.SSS")
    private String eventType; // text
    private Double powerUsed; // real
    private String anyValue; // text

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getSimulationId() {
        return simulationId;
    }

    public void setSimulationId(long simulationId) {
        this.simulationId = simulationId;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public String getActorType() {
        return actorType;
    }

    public void setActorType(String actorType) {
        this.actorType = actorType;
    }

    public String getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(String submitDate) {
        this.submitDate = submitDate;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Double getPowerUsed() {
        return powerUsed;
    }

    public void setPowerUsed(Double powerUsed) {
        this.powerUsed = powerUsed;
    }

    public String getAnyValue() {
        return anyValue;
    }

    public void setAnyValue(String anyValue) {
        this.anyValue = anyValue;
    }

    public SimulationLog() {}

    public SimulationLog(int id, int simulationId, String actorName, String actorType, String submitDate, String eventType, Double powerUsed, String anyValue) {
        this.id = id;
        this.simulationId = simulationId;
        this.actorName = actorName;
        this.actorType = actorType;
        this.submitDate = submitDate;
        this.eventType = eventType;
        this.powerUsed = powerUsed;
        this.anyValue = anyValue;
    }

    @Override
    public String toString() {
        return "SimulationLog{" +
                "id=" + id +
                ", simulationId=" + simulationId +
                ", actorName='" + actorName + '\'' +
                ", actorType='" + actorType + '\'' +
                ", submitDate='" + submitDate + '\'' +
                ", eventType='" + eventType + '\'' +
                ", powerUsed=" + powerUsed +
                ", anyValue='" + anyValue + '\'' +
                '}';
    }
}
