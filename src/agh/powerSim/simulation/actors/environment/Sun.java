package agh.powerSim.simulation.actors.environment;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;

import org.joda.time.Interval;
import org.joda.time.LocalDateTime;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

public class Sun implements WeatherElement<Double> {

    private static final long PRE_SUNRISE_DURATION = 30;
    
    private static final long POST_SUNRISE_DURATION = 20;
    
    private static final long PRE_SUNSET_DURATION = 30;
    
    private static final long POST_SUNSET_DURATION = 20;
    
    protected LocalDateTime sunriseTime;
    
    protected LocalDateTime sunsetTime;
    
    protected Location location;
    
    protected SunriseSunsetCalculator sunriseSunsetCalculator;

    protected LocalDateTime currentDate;

    public double sunLighLevel = 0.0;
    
    protected double latitude = 0.0;
    
    protected double longitude = 0.0;

    protected Properties properties = new Properties();
    
    public Sun(String properties){
    	this.properties = new Properties();
    	try {
			this.properties.load(new FileInputStream(properties));

			latitude = Double.parseDouble(this.properties.getProperty("latitude"));
			longitude = Double.parseDouble(this.properties.getProperty("longitude"));

			location = new Location(latitude,longitude);
			
			sunriseSunsetCalculator = new SunriseSunsetCalculator(location, Calendar.getInstance().getTimeZone());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

    }
    

	private static double sunriseInterpolation(long sec){
		if(sec> (POST_SUNRISE_DURATION+PRE_SUNRISE_DURATION)*60)
			return 100.0;
		double x = ((double)sec)/60;
		double res = - Math.pow(x, 5.0)/480000+Math.pow(x, 4.0)/6000-Math.pow(x, 3.0)/320+Math.pow(x, 2.0)/30+x/3;
		return res<0.0?0.0:res;
	}

	private static double sunsetInterpolation(long sec){
		if(sec> (POST_SUNSET_DURATION+PRE_SUNSET_DURATION)*60)
			return 0.0;
		double x = ((double)sec)/60;
		double res =  - Math.pow(x, 5.0)/480000+11*Math.pow(x, 4.0)/48000-37*Math.pow(x, 3.0)/4800+49*Math.pow(x, 2.0)/480-83*x/24+100;
		return res>100.0?100.0:res;
	}


	public void processTime(LocalDateTime time, double deltaTime) {
		if(currentDate==null){
        	setSunriseSunsetTimes(time);           	
        } else {
            Interval interval = new Interval(currentDate.toDateTime(), time.toDateTime());
            if(interval.toDuration().getStandardDays()>0){
            	setSunriseSunsetTimes(time);
            }   
        }
        if(time.isBefore(sunriseTime)){
        	//before sunrise
        	Interval interval = new Interval(time.toDateTime(), sunriseTime.toDateTime());
        	if(interval.toDuration().getStandardMinutes() <= PRE_SUNRISE_DURATION){
        		sunLighLevel = sunriseInterpolation(PRE_SUNRISE_DURATION*60-interval.toDuration().getStandardSeconds());
        		//log.warning("SUNRISE: "+sunLighLevel);
        	} else {
        		sunLighLevel = 0.0;
        	}
        } else if(time.isBefore(sunsetTime)){
        	//after sunrise
        	Interval interval = new Interval(sunriseTime.toDateTime(),time.toDateTime());
        	if(interval.toDuration().getStandardMinutes() <= POST_SUNRISE_DURATION){
        		sunLighLevel = sunriseInterpolation(PRE_SUNRISE_DURATION*60+interval.toDuration().getStandardSeconds());
        		//log.warning("SUNRISE: "+sunLighLevel);
        	} else {
        	
            	//before sunset
            	Interval interval2 = new Interval(time.toDateTime(),sunsetTime.toDateTime());
            	if(interval2.toDuration().getStandardMinutes() < PRE_SUNSET_DURATION){
            		sunLighLevel = sunsetInterpolation(PRE_SUNSET_DURATION*60-interval2.toDuration().getStandardSeconds());
            		//log.warning("SUNSET: "+sunLighLevel);
            	} else {
            		sunLighLevel = 100.0;
            	}
        	}
        } else {
        	//after sunset
        	Interval interval = new Interval(sunsetTime.toDateTime(),time.toDateTime());
        	if(interval.toDuration().getStandardMinutes() < POST_SUNSET_DURATION){
        		sunLighLevel = sunsetInterpolation(PRE_SUNSET_DURATION*60+interval.toDuration().getStandardSeconds());
        		//log.warning("SUNSET: "+sunLighLevel);
        	} else {
        		sunLighLevel = 0.0;
        	}
    	}
	}
	

	private void setSunriseSunsetTimes(LocalDateTime time){
    	currentDate=time;
    	Calendar c =Calendar.getInstance();
    	c.setTime(time.toDate());
    	String[] sunriseString = sunriseSunsetCalculator.getAstronomicalSunriseForDate(c).split(":");
    	String[] sunsetString = sunriseSunsetCalculator.getAstronomicalSunsetForDate(c).split(":");
    	c.set(time.getYear(), time.getMonthOfYear()-1, time.getDayOfMonth(), Integer.parseInt(sunriseString[0]), Integer.parseInt(sunriseString[1]));
    	sunriseTime = LocalDateTime.fromCalendarFields(c);
    	c.set(time.getYear(), time.getMonthOfYear()-1, time.getDayOfMonth(), Integer.parseInt(sunsetString[0]), Integer.parseInt(sunsetString[1]));
    	sunsetTime = LocalDateTime.fromCalendarFields(c);
		
	}


	@Override
	public String getForecast() {
		return "";
	}


	@Override
	public void setRelatedElement(WeatherElement element) {
		
	}


	@Override
	public Double getValue() {
		return sunLighLevel;
	}
	

}
