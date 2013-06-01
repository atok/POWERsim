package agh.powerSim.simulation.actors.environment;

import java.util.Random;

import org.joda.time.LocalDateTime;

public class Front implements WeatherElement<Boolean> {
	
	protected LocalDateTime pressureChangeTime;
	
	protected boolean highPressureArea = true;
	
	protected void prepareAtmosphericFront(LocalDateTime time, double deltaTime){
		if(pressureChangeTime == null){
			pressureChangeTime = time;
		}
		if(pressureChangeTime.isBefore(time) || pressureChangeTime.isEqual(time)){
			Random frontRandom = new Random(time.getMillisOfDay());
			highPressureArea = frontRandom.nextBoolean();
			int hours = 4+frontRandom.nextInt(8);
			if(deltaTime>hours*3600){
				hours=(int)(deltaTime/3600);
			}
			pressureChangeTime=pressureChangeTime.plusHours(hours);
		}
	}

	public String getForecast() {
		return (highPressureArea?"High pressure":"Low pressure") + " till: "+pressureChangeTime;
	}

	public void processTime(LocalDateTime time, double deltaTime) {
		prepareAtmosphericFront(time,deltaTime);
		
	}

	@Override
	public void setRelatedElement(WeatherElement element) {
	}

	@Override
	public Boolean getValue() {
		return highPressureArea;
	}
	
}
