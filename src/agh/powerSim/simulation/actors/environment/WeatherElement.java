package agh.powerSim.simulation.actors.environment;

import org.joda.time.LocalDateTime;

public interface WeatherElement<T> {
	public void processTime(LocalDateTime time, double deltaTime);
	
	public String getForecast();
	
	public void setRelatedElement(WeatherElement element);
	
	public T getValue();
	
}
