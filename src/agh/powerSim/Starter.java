package agh.powerSim;

import agh.powerSim.simulation.Simulation;
import agh.powerSim.simulation.actors.SleepActor;
import agh.powerSim.simulation.actors.House;
import agh.powerSim.simulation.actors.devices.ElectricHeater;
import agh.powerSim.simulation.actors.devices.TelevisionSet;
import agh.powerSim.simulation.actors.devices.WaterHeater;
import agh.powerSim.simulation.actors.humans.*;
import agh.powerSim.simulation.actors.devices.Lamp;
import agh.powerSim.simulation.actors.environment.WeatherActor;
import akka.actor.ActorRef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.joda.time.DateTime;
import scala.collection.immutable.Nil;

public class Starter {

	public static void main(String[] args) {
		Simulation sim = new Simulation();

        Map<String,String> parametersLamp = new HashMap<String, String>();
        parametersLamp.put("lightGen", "1000.0");
        Map<String,String> parametersHeater = new HashMap<String, String>();

		final ActorRef house = sim.addHouse(House.class, "House:house-1");
		final ActorRef lamp1 = sim.addDevice(Lamp.class, "Lamp:lamp-1", house, parametersLamp);
		final ActorRef lamp2 = sim.addDevice(Lamp.class, "Lamp:lamp-2", house, parametersLamp);
		final ActorRef lamp3 = sim.addDevice(Lamp.class, "Lamp:lamp-3", house, parametersLamp);
		final ActorRef heater = sim.addDevice(ElectricHeater.class, "Heater:heater-1", house, parametersHeater);
        final ActorRef waterHeater = sim.addDevice(WaterHeater.class, "WaterHeater:wh-1", house, new HashMap<String,String>());
        final ActorRef tv = sim.addDevice(TelevisionSet.class, "TV:tv-1", house, new HashMap<String, String>());

		final ArrayList<Human.DeviceToken> devices = new ArrayList<Human.DeviceToken>();
		devices.add(new Human.DeviceToken(Lamp.class, lamp1));
		devices.add(new Human.DeviceToken(Lamp.class, lamp2));
		devices.add(new Human.DeviceToken(Lamp.class, lamp3));
		devices.add(new Human.DeviceToken(ElectricHeater.class, heater));
        devices.add(new Human.DeviceToken(WaterHeater.class, waterHeater));
        devices.add(new Human.DeviceToken(TelevisionSet.class, tv));
		// sleeping: every day of every month from 23.10 6 hours
		HumanStateChangeTime sleepTime = new HumanStateChangeTime(HumanState.SLEEPING, new DateTime(1, 1, 1, 23, 10), 1000 * 60 * 60 * 6, new Integer[] {}, new Integer[] {}, "Sleep time");
		// work: from monday to friday every month exept July from 8:00 9 hours
		HumanStateChangeTime workTime1 = new HumanStateChangeTime(HumanState.OUTSIDE, new DateTime(1, 1, 1, 8, 0), 1000 * 60 * 60 * 9, new Integer[] { -6, -7 }, new Integer[] { -7 }, "Work time");
		// work: from monday to friday only from janury to march from 12:00 till
		// 18.00
		HumanStateChangeTime workTime2 = new HumanStateChangeTime(HumanState.OUTSIDE, new DateTime(1, 1, 1, 12, 0), new DateTime(1, 1, 1, 18, 0), new Integer[] { 1, 2, 3, 4, 5 }, new Integer[] { 1, 2, 3 }, "Work time");

		sim.addHuman(Human.class, "Human:Jonh", house, devices, new HumanCharacter(), new HumanStateChangeTime[] { sleepTime, workTime1 });
		sim.addHuman(Human.class, "Human:Betty", house, devices, new HumanCharacter(), new HumanStateChangeTime[] { sleepTime, workTime2 });

		sim.addActor(SleepActor.class, "slowdown");
		sim.start();
	}

}
