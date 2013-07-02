package agh.powerSim.simulation.actors.utils;

public class CalculateUtils {
	/**
	 * 
	 * @param power moc zużywana przez użądzenie [W]
	 * @param deltaTime kwant czasu [s]
	 * @return zużycie mocy po czasie deltaTime [s] w kWh
	 */
	public static double powerUsage(double power, double deltaTime){
		return (power*deltaTime)/3600000;
	}
}
