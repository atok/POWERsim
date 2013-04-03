package agh.powerSim;


import agh.powerSim.simulation.Simulation;
import agh.powerSim.simulation.actors.ExampleActor;
import agh.powerSim.simulation.actors.House;
import agh.powerSim.simulation.actors.Human;
import agh.powerSim.simulation.actors.devices.Lamp;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;

import java.util.ArrayList;

public class Starter {

    public static void main(String [] args) {
        Simulation sim = new Simulation();

        final ActorRef house = sim.addActor(House.class, "house-1");
        final ActorRef lamp =  sim.addDevice(Lamp.class, "lamp-1", house);

        final ArrayList<Human.DeviceToken> devices = new ArrayList<Human.DeviceToken>();
        devices.add(new Human.DeviceToken(Lamp.class, lamp));

        sim.addHuman(Human.class, "human-1", house, devices);

        sim.addActor(ExampleActor.class, "slowdown");
        sim.start();
    }

}
