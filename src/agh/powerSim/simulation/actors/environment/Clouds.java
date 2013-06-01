package agh.powerSim.simulation.actors.environment;

import java.util.Random;

import org.joda.time.Interval;
import org.joda.time.LocalDateTime;

import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Clouds implements WeatherElement<Double> {

	public double cloudCoverage = 0.0;
	
	protected LocalDateTime cloudCoverageChangeTime;
	
	protected double futureCloudeCoverage = 0.0;
	
	private Front front;
	
	public void setFront(Front front){
		this.front=front;
	}
	
	public Clouds(){
		
	}
	
	public synchronized void processTime(LocalDateTime time, double deltaTime){
		prepareCloudeCoverage(time, deltaTime);
		
		Interval interval = new Interval(time.toDateTime(), cloudCoverageChangeTime.toDateTime());
		long seconds = interval.toDuration().getStandardSeconds();
		if(seconds==0){
			seconds=1;
		}
		
		cloudCoverage = ((futureCloudeCoverage-cloudCoverage)/seconds)*deltaTime+cloudCoverage;
		cloudCoverage = cloudCoverage>=0.0?cloudCoverage:0.0;
		cloudCoverage = cloudCoverage<=100.0?cloudCoverage:100.0;
	}
	
	protected void prepareCloudeCoverage(LocalDateTime time, double timeDelta){
		if(cloudCoverageChangeTime == null){
			cloudCoverageChangeTime = time;
		}
		if(cloudCoverageChangeTime.isBefore(time) || cloudCoverageChangeTime.isEqual(time)){
			Random coverageRandom = new Random(time.getMillisOfDay());
			if(front.getValue()){
				futureCloudeCoverage = futureCloudeCoverage - (coverageRandom.nextDouble()*futureCloudeCoverage) + (coverageRandom.nextDouble()*20-10);
				futureCloudeCoverage = (futureCloudeCoverage>=0.0?futureCloudeCoverage:0.0);
			} else {
				futureCloudeCoverage = futureCloudeCoverage + (coverageRandom.nextDouble()*(100.0-futureCloudeCoverage)) + (coverageRandom.nextDouble()*20-10);				
				futureCloudeCoverage = (futureCloudeCoverage<=100.0?futureCloudeCoverage:100.0);
			}
			futureCloudeCoverage = round(futureCloudeCoverage);
			int minutes = 15+coverageRandom.nextInt(45);
			if(timeDelta>minutes*60){
				minutes+=(int)(timeDelta/60);
			}
			cloudCoverageChangeTime=cloudCoverageChangeTime.plusMinutes(minutes);
		}
	}
	
	public String getForecast(){
		return "WEATHER: " + cloudCoverageChangeTime + ": cloud coverage: " + round(futureCloudeCoverage) + ": " + front.getForecast();
	}
	
	
	private double round(double x){
		double y = x;
		y *= 100;
        y = Math.round(y);
        y /= 100;
        return y;
	}

	@Override
	public void setRelatedElement(WeatherElement element) {	
		if(element instanceof Front){
			setFront((Front) element);
		}
	}

	@Override
	public Double getValue() {
		return round(cloudCoverage);
	}
}
