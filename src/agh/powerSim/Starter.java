package agh.powerSim;

import agh.powerSim.simulation.Simulation;
import agh.powerSim.simulation.actors.SleepActor;
import agh.powerSim.simulation.actors.House;
import agh.powerSim.simulation.actors.devices.ElectricHeater;
import agh.powerSim.simulation.actors.humans.Human;
import agh.powerSim.simulation.actors.humans.HumanCharacter;
import agh.powerSim.simulation.actors.humans.HumanState;
import agh.powerSim.simulation.actors.humans.HumanStateChangeTime;
import agh.powerSim.simulation.actors.devices.Lamp;
import agh.powerSim.simulation.actors.environment.WeatherActor;
import akka.actor.ActorRef;

import java.util.ArrayList;

import org.joda.time.DateTime;

public class Starter {

	public static void main(String[] args) {
		Simulation sim = new Simulation();

		final ActorRef house = sim.addHouse(House.class, "house-1");
		final ActorRef lamp1 = sim.addDevice(Lamp.class, "lamp-1", house);
		final ActorRef lamp2 = sim.addDevice(Lamp.class, "lamp-2", house);
		final ActorRef lamp3 = sim.addDevice(Lamp.class, "lamp-3", house);
		final ActorRef heater = sim.addDevice(ElectricHeater.class, "heater-1", house);

		final ArrayList<Human.DeviceToken> devices = new ArrayList<Human.DeviceToken>();
		devices.add(new Human.DeviceToken(Lamp.class, lamp1));
		devices.add(new Human.DeviceToken(Lamp.class, lamp2));
		devices.add(new Human.DeviceToken(Lamp.class, lamp3));
		devices.add(new Human.DeviceToken(ElectricHeater.class, heater));
		// sleeping: every day of every month from 23.10 6 hours
		HumanStateChangeTime sleepTime = new HumanStateChangeTime(HumanState.SLEEPING, new DateTime(1, 1, 1, 23, 10), 1000 * 60 * 60 * 6, new Integer[] {}, new Integer[] {}, "Sleep time");
		// work: from monday to friday every month exept July from 8:00 9 hours
		HumanStateChangeTime workTime1 = new HumanStateChangeTime(HumanState.OUTSIDE, new DateTime(1, 1, 1, 8, 0), 1000 * 60 * 60 * 9, new Integer[] { -6, -7 }, new Integer[] { -7 }, "Work time");
		// work: from monday to friday only from janury to march from 12:00 till
		// 18.00
		HumanStateChangeTime workTime2 = new HumanStateChangeTime(HumanState.OUTSIDE, new DateTime(1, 1, 1, 12, 0), new DateTime(1, 1, 1, 18, 0), new Integer[] { 1, 2, 3, 4, 5 }, new Integer[] { 1, 2, 3 }, "Work time");

		sim.addHuman(Human.class, "Jonh", house, devices, new HumanCharacter(), new HumanStateChangeTime[] { sleepTime, workTime1 });
		sim.addHuman(Human.class, "Betty", house, devices, new HumanCharacter(), new HumanStateChangeTime[] { sleepTime, workTime2 });

		sim.addActor(SleepActor.class, "slowdown");
		sim.start();
	}

}
