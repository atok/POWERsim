package agh.powerSim.simulation.actors.environment;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.joda.time.Interval;
import org.joda.time.LocalDateTime;

import scala.util.Random;

public class Temperature implements WeatherElement {
	
	private static final String LOW = "l";

	private static final String MID = "m";

	private static final String HIGH = "h";

	private Properties properties = new Properties();

	private Map<Integer, Map<String, Double>> temperatureParams = new HashMap<>();
	
	private int mthIdx = 0;
	
	private Front front;
	
	private Clouds clouds;
	
	private double temperature = 0.0;
	
	private LocalDateTime forecastTime;
	
	private double forecast = 0.0;
	
	public Temperature(String properties) {
		try {
			this.properties.load(new FileInputStream(properties));

			parseProperty("january");
			parseProperty("february");
			parseProperty("march");
			parseProperty("april");
			parseProperty("may");
			parseProperty("june");
			parseProperty("july");
			parseProperty("august");
			parseProperty("september");
			parseProperty("october");
			parseProperty("november");
			parseProperty("december");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void parseProperty(String prop){
		String tmp = properties.getProperty(prop);
		String[] val = tmp.split(";");
		Map<String, Double> tempsMap = new HashMap<>();
		tempsMap.put(HIGH, Double.parseDouble(val[0]));
		tempsMap.put(MID, Double.parseDouble(val[1]));
		tempsMap.put(LOW, Double.parseDouble(val[2]));
		temperatureParams.put(mthIdx++, tempsMap);
		
	}

	public void setFront(Front front) {
		this.front=front;
		
	}

	public void setClouds(Clouds clouds) {
		this.clouds=clouds;
	}

	@Override
	public void processTime(LocalDateTime time, double deltaTime) {
		if(forecastTime==null || forecastTime.isEqual(time) ||forecastTime.isBefore(time)){
			prepareForecast(time,deltaTime);
		}

		Interval interval = new Interval(time.toDateTime(), forecastTime.toDateTime());
		long seconds = interval.toDuration().getStandardSeconds();
		if(seconds==0){
			seconds=1;
		}
		double deltaTemp = forecast-temperature;
		deltaTemp = deltaTemp/seconds;
		deltaTemp = deltaTemp*deltaTime;
		temperature = deltaTemp+temperature;
	}

	private void prepareForecast(LocalDateTime time, double deltaTime) {
		Map<String, Double> temps = temperatureParams.get(time.getMonthOfYear());
		Random random = new Random(time.getMillisOfDay());
		int minutes = 60+random.nextInt(120);
		if(minutes*60<deltaTime){
			minutes+=deltaTime/60;
		}
		if(forecastTime==null){
			forecastTime=time;
		}
		forecastTime = forecastTime.plusMinutes(minutes);
		if(front.getValue()){
			forecast =  temps.get(MID)+random.nextDouble()*(temps.get(HIGH)-temps.get(MID));
		} else {
			forecast =  temps.get(LOW)+random.nextDouble()*(temps.get(MID)-temps.get(LOW));
		}
		if(forecastTime.getHourOfDay()>12 && forecastTime.getHourOfDay()<15){
			if(forecastTime.getMonthOfYear()>=5 && forecastTime.getMonthOfYear()<=8){
				forecast += random.nextDouble()*15.0;
			} 
		} else if(forecastTime.getHourOfDay()>22 || forecastTime.getHourOfDay()<5){
			if(forecastTime.getMonthOfYear()<=2 || forecastTime.getMonthOfYear()>=11){
				forecast -= random.nextDouble()*10.0;
			} else {
				forecast -= random.nextDouble()*5.0;				
			}
		}
		if(clouds.getValue()>70.0){
			forecast-=random.nextDouble()*clouds.getValue()/10;
		}
		
	}

	@Override
	public String getForecast() {
		return "TEMPERATURE OUTSIDE: "+round(temperature)+"C"+" FORECAST "+round(forecast)+"C at "+forecastTime;
	}

	@Override
	public void setRelatedElement(WeatherElement element) {
		if(element instanceof Front){
			setFront((Front) element);
		} else if (element instanceof Clouds){
			setClouds((Clouds) element);
		}
		
	}

	private double round(double temperature){
		double temperature2 = temperature*100;
		temperature2 = Math.round(temperature2);
		temperature2 = temperature2/100;
		return temperature2;
	}
	
	@Override
	public Double getValue() {
		return round(temperature);
	}

}
