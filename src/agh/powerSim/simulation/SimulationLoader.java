package agh.powerSim.simulation;

import agh.powerSim.simulation.actors.House;
import agh.powerSim.simulation.actors.humans.*;
import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.prefs.Preferences;

/**
 * Created with IntelliJ IDEA.
 * User: Khajiit
 * Date: 23.05.13
 * Time: 12:02
 * To change this template use File | Settings | File Templates.
 */
public class SimulationLoader {

    private static final String HOUSES = "houses";
    private static final String HUMANS = "humans";

    private static final String LIGHT_TRESHOLD = "lightComfortTreshold";
    private static final String ENTERTAINMENT = "entertainment";
    private static final String HUNGER = "hunger";
    private static final String WORK_DUTY = "workDuty";
    private static final String CLEANER = "cleaner";
    private static final String WARM_TRESHOLD = "warmComfortTreshold";
    private static final String HOUSE_ID = "id";
    private static final String HUMAN_ID = "id";
    private static final String DEVICES = "devices";
    private static final String DEVICE_ID = "id";
    private static final String DEVICE_CLASS = "class";

    private static final String HUMAN_STATES = "states";
    private static final String STATE = "state";
    private static final String FROM_TIME = "fromTime";
    private static final String TILL_TIME = "tillTime";
    private static final String DURATION = "duration";
    private static final String DAYS = "days";
    private static final String MONTHS = "months";
    private static final String COMMENT = "comment";

    private static final String STATE_DATE_FORMAT = "HH:mm";

    private SimulationLoaderContext loaderContext = new SimulationLoaderContext();

    public Simulation loadSimulation(String filePath) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(new File(filePath));
        Simulation simulation = new Simulation();

        loadHouseholds(rootNode, simulation);

        return null;
    }

    private void loadHouseholds(JsonNode rootNode, Simulation simulation) throws Exception {
        JsonNode housesNode = rootNode.path(HOUSES);
        if(!housesNode.isContainerNode()) {
            throw new Exception("Houses node is not container node");
        }

        Iterator<JsonNode> houses = housesNode.elements();
        while(houses.hasNext()) {
            loaderContext.clearContext();
            JsonNode houseNode = houses.next();
            createHouse(houseNode, simulation);
            loadDevices(houseNode, simulation);
            loadHumans(houseNode, simulation);
        }
    }

    private void loadDevices(JsonNode houseNode, Simulation simulation) throws Exception {
        JsonNode devicesNode = houseNode.path(DEVICES);
        if(!devicesNode.isContainerNode()) {
            throw new Exception("Devices node is not container node");
        }

        Iterator<JsonNode> devices = devicesNode.elements();
        while(devices.hasNext()) {
            JsonNode device = devices.next();
            String deviceId = device.path(DEVICE_ID).asText();
            String deviceClassName = device.path(DEVICE_CLASS).asText();
            System.out.println("Creating device " + deviceId + " of class " + deviceClassName);
            Class deviceClass = Class.forName(deviceClassName);
            ActorRef deviceActorRef = simulation.addDevice(deviceClass, deviceId, loaderContext.currentHouse);
            loaderContext.devicesInHouse.add(new Human.DeviceToken(deviceClass, deviceActorRef));
        }
    }

    private void createHouse(JsonNode houseNode, Simulation simulation) throws Exception {
        String houseId = houseNode.path(HOUSE_ID).asText();
        System.out.println("Creating household " + houseId);
        loaderContext.currentHouse = simulation.addHouse(House.class, houseId);
    }

    private void loadHumans(JsonNode rootNode, Simulation simulation) throws Exception {
        JsonNode humansNode = rootNode.path(HUMANS);
        ArrayList<HumanCharacter> humanList = new ArrayList<HumanCharacter>();

        if(humansNode.isContainerNode()) {
            Iterator<JsonNode> humans = humansNode.elements();
            while(humans.hasNext()) {
                JsonNode human = humans.next();

                String humanId = human.path(HUMAN_ID).asText();

                JsonNode currentValue;
                HumanCharacter humanCharacter = new HumanCharacter();
                currentValue = human.path(LIGHT_TRESHOLD);
                humanCharacter.setLightComfortTreshold(currentValue.asInt());  //default 0, no exceptions thrown
                currentValue = human.path(ENTERTAINMENT);
                humanCharacter.setEntertainment(currentValue.asInt());
                currentValue = human.path(HUNGER);
                humanCharacter.setHunger(currentValue.asInt());
                currentValue = human.path(WORK_DUTY);
                humanCharacter.setWorkDuty(currentValue.asInt());
                currentValue = human.path(CLEANER);
                humanCharacter.setCleaner(currentValue.asInt());
                currentValue = human.path(WARM_TRESHOLD);
                humanCharacter.setWarmComfortTreshold(currentValue.asDouble());

                simulation.addHuman(Human.class, humanId, loaderContext.currentHouse, loaderContext.devicesInHouse, humanCharacter, loadHumanStateChangeTimes(human));
            }

        } else {
            throw new Exception("Humans node is not container node");
        }
    }

    private HumanStateChangeTime[] loadHumanStateChangeTimes(JsonNode humanNode) throws Exception {
        List<HumanStateChangeTime> humanStateList = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat(STATE_DATE_FORMAT);
        JsonNode humanStates = humanNode.path(HUMAN_STATES);
        if(humanStates.isContainerNode()) {
            for(JsonNode state : humanStates) {
                String stateName = state.path(STATE).asText();
                HumanState humanState = HumanState.valueOf(stateName);

                String fromTime = state.path(FROM_TIME).asText();
                Date fromDate = dateFormat.parse(fromTime);

                String tillTime = null;
                Date tillDate = null;
                Integer duration = null;
                if(state.hasNonNull(FROM_TIME)) {
                    tillTime = state.path(FROM_TIME).asText();
                    tillDate = dateFormat.parse(fromTime);
                } else {
                    duration = state.path(DURATION).asInt();
                }

                String comment = state.path(COMMENT).asText();
                Integer[] days = nodeAsArray(state.path(DAYS));
                Integer[] months = nodeAsArray(state.path(MONTHS));

                HumanStateChangeTime stateChangeTime;
                if(tillTime != null) {
                    stateChangeTime = new HumanStateChangeTime(humanState, new DateTime(fromDate.getTime()), new DateTime(tillDate.getTime()), days, months, comment);
                } else {
                    stateChangeTime = new HumanStateChangeTime(humanState, new DateTime(fromDate.getTime()), duration, days, months, comment);
                }

                humanStateList.add(stateChangeTime);
            }
        } else {
            throw new Exception("Human states node is not container node");
        }

        return humanStateList.toArray(new HumanStateChangeTime[humanStateList.size()]);
    }

    private Integer[] nodeAsArray(JsonNode node) throws Exception {
        if(!node.isContainerNode()) {
            throw new Exception("Node is not container node");
        }

        List<Integer> array = new ArrayList<>();
        Iterator<JsonNode> iterator = node.elements();
        while(iterator.hasNext()) {
            array.add(iterator.next().asInt());
        }
        return (Integer[]) array.toArray(new Integer[array.size()]);
    }

    private class SimulationLoaderContext {
        ActorRef currentHouse;
        ArrayList<Human.DeviceToken> devicesInHouse;

        public void clearContext() {
            currentHouse = null;
            devicesInHouse = new ArrayList<Human.DeviceToken>();
        }
    }

}
