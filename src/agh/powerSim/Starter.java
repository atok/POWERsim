package agh.powerSim;


import agh.powerSim.simulation.Simulation;
import agh.powerSim.simulation.actors.SleepActor;
import agh.powerSim.simulation.actors.House;
import agh.powerSim.simulation.actors.devices.ElectricHeater;
import agh.powerSim.simulation.actors.humans.Human;
import agh.powerSim.simulation.actors.devices.Lamp;
import akka.actor.ActorRef;

import java.util.ArrayList;

public class Starter {

    public static void main(String [] args) {
        Simulation sim = new Simulation();

        final ActorRef house = sim.addHouse(House.class, "house-1");
        final ActorRef lamp1 =  sim.addDevice(Lamp.class, "lamp-1", house);
        final ActorRef lamp2 =  sim.addDevice(Lamp.class, "lamp-2", house);
        final ActorRef lamp3 =  sim.addDevice(Lamp.class, "lamp-3", house);
        final ActorRef heater = sim.addDevice(ElectricHeater.class, "heater-1", house);

        final ArrayList<Human.DeviceToken> devices = new ArrayList<Human.DeviceToken>();
        devices.add(new Human.DeviceToken(Lamp.class, lamp1));
        devices.add(new Human.DeviceToken(Lamp.class, lamp2));
        devices.add(new Human.DeviceToken(Lamp.class, lamp3));
        devices.add(new Human.DeviceToken(ElectricHeater.class, heater));

        sim.addHuman(Human.class, "human-1", house, devices);

        sim.addActor(SleepActor.class, "slowdown");
        sim.start();
    }

}
