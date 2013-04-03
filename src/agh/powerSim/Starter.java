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

        Props p1 = new Props(new UntypedActorFactory() {
            public UntypedActor create() {
                return new House();
            }
        });
        final ActorRef house = sim.addActor(p1, "house1");

        Props p2 = new Props(new UntypedActorFactory() {
            public UntypedActor create() {
                return new Lamp(house);
            }
        });
        final ActorRef lamp = sim.addActor(p2, "lamp");
        final ArrayList<Human.DeviceToken> devices = new ArrayList<Human.DeviceToken>();
        devices.add(new Human.DeviceToken(Lamp.class, lamp));

        Props p3 = new Props(new UntypedActorFactory() {
            public UntypedActor create() {
                return new Human(house, devices);
            }
        });
        sim.addActor(p3, "human");



        sim.addActor(ExampleActor.class, "slowdown");
        sim.start();
    }

}
