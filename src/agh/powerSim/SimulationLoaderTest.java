package agh.powerSim;

import agh.powerSim.simulation.Simulation;
import agh.powerSim.simulation.SimulationLoader;

/**
 * Created with IntelliJ IDEA.
 * User: Khajiit
 * Date: 23.05.13
 * Time: 14:35
 * To change this template use File | Settings | File Templates.
 */
public class SimulationLoaderTest {

    public static void main(String [] args) {
        SimulationLoader simulationLoader = new SimulationLoader();
        Simulation simulation;
        try {
            simulation = simulationLoader.loadSimulation("D:\\Projects\\Java\\SmartGrid\\POWERsim\\test.json");
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
