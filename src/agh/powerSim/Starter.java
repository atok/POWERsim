package agh.powerSim;


import agh.powerSim.simulation.Simulation;
import agh.powerSim.simulation.actors.ExampleActor;

public class Starter {

    public static void main(String [] args) {
        Simulation sim = new Simulation();
        sim.addActor(ExampleActor.class, "example1");
        sim.addActor(ExampleActor.class, "example2");
        sim.addActor(ExampleActor.class, "example3");
        sim.start();
    }

}
