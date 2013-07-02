package agh.powerSim;

import agh.powerSim.simulation.actors.utils.DataRecorder;

/**
 * Created with IntelliJ IDEA.
 * User: Karol
 * Date: 03.05.13
 * Time: 13:41
 */
public class RecorderStarter {
    public static void main(String[] args) throws Exception {
        DataRecorder dao = new DataRecorder();
        System.out.println(dao.getSimulationId());
    }

}
