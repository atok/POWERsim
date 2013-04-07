package agh.powerSim;


import java.util.ArrayList;

import agh.powerSim.gui.Context;
import agh.powerSim.simulation.Simulation;
import agh.powerSim.simulation.actors.House;
import agh.powerSim.simulation.actors.SleepActor;
import agh.powerSim.simulation.actors.devices.Lamp;
import agh.powerSim.simulation.actors.devices.LampView;
import agh.powerSim.simulation.actors.humans.Human;
import akka.actor.ActorRef;

public class WindowStarter {

    public static void main(String [] args) {
    	Context.launch();
    }
    
    public static void run(){

        Simulation sim = new Simulation();

        final ActorRef house = sim.addHouse(House.class, "house-1");
        final ActorRef lamp =  sim.addDevice(LampView.class, "lamp-1", house);
        final ActorRef lamp2 =  sim.addDevice(LampView.class, "lamp-2", house);

        final ArrayList<Human.DeviceToken> devices = new ArrayList<Human.DeviceToken>();
        devices.add(new Human.DeviceToken(Lamp.class, lamp));

        sim.addHuman(Human.class, "human-1", house, devices);

        sim.addActor(SleepActor.class, "slowdown");
        sim.start();
    }

}
