package agh.powerSim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

import agh.powerSim.simulation.Simulation;
import agh.powerSim.simulation.actors.House;
import agh.powerSim.simulation.actors.SleepActor;
import agh.powerSim.simulation.actors.devices.DishWasher;
import agh.powerSim.simulation.actors.devices.ElectricHeater;
import agh.powerSim.simulation.actors.devices.ElectricKettle;
import agh.powerSim.simulation.actors.devices.Fridge;
import agh.powerSim.simulation.actors.devices.Lamp;
import agh.powerSim.simulation.actors.devices.MicrowaveOven;
import agh.powerSim.simulation.actors.devices.MobileDevice;
import agh.powerSim.simulation.actors.devices.Oven;
import agh.powerSim.simulation.actors.devices.RadioSet;
import agh.powerSim.simulation.actors.devices.TelevisionSet;
import agh.powerSim.simulation.actors.devices.VacumCliner;
import agh.powerSim.simulation.actors.devices.WashingMashine;
import agh.powerSim.simulation.actors.devices.WaterHeater;
import agh.powerSim.simulation.actors.humans.Human;
import agh.powerSim.simulation.actors.humans.HumanCharacter;
import agh.powerSim.simulation.actors.humans.HumanState;
import agh.powerSim.simulation.actors.humans.HumanStateChangeTime;
import akka.actor.ActorRef;

public class Starter {

	public static void main(String[] args) {
		Simulation sim = new Simulation();

		Map<String, String> parametersLamp = new HashMap<String, String>();
		parametersLamp.put("lightGen", "1000.0");
		Map<String, String> emptyPartams = new HashMap<String, String>();

		final ActorRef house = sim.addHouse(House.class, "House:house-1");
		final ActorRef lamp1 = sim.addDevice(Lamp.class, "Lamp:lamp-1", house, parametersLamp);
		final ActorRef lamp2 = sim.addDevice(Lamp.class, "Lamp:lamp-2", house, parametersLamp);
		final ActorRef lamp3 = sim.addDevice(Lamp.class, "Lamp:lamp-3", house, parametersLamp);
		final ActorRef heater = sim.addDevice(ElectricHeater.class, "Heater:heater-1", house, emptyPartams);
        final ActorRef tv = sim.addDevice(TelevisionSet.class, "TV:tv-1", house, new HashMap<String, String>());
        final ActorRef kettle = sim.addDevice(ElectricKettle.class, "Kettle:kettle", house, emptyPartams);
		final ActorRef fridge = sim.addDevice(Fridge.class, "Fridge:fridge", house, emptyPartams);
		final ActorRef microwave = sim.addDevice(MicrowaveOven.class, "MicrowaveOven:microwave", house, emptyPartams);
		final ActorRef waterHeater = sim.addDevice(WaterHeater.class, "WaterHeater:wh-1", house, new HashMap<String, String>());
		final ActorRef dishWasher = sim.addDevice(DishWasher.class, "DishWasher:dishWasher-1", house, new HashMap<String, String>());
		final ActorRef mobile = sim.addDevice(MobileDevice.class, "MobileDevice:Smartphone-1", house, new HashMap<String, String>());
		final ActorRef oven = sim.addDevice(Oven.class, "Oven:oven-1", house, new HashMap<String, String>());
		final ActorRef radio = sim.addDevice(RadioSet.class, "RadioSet:radio-1", house, new HashMap<String, String>());
		final ActorRef vacum = sim.addDevice(VacumCliner.class, "VacumCliner:vacum-1", house, new HashMap<String, String>());
		final ActorRef wm = sim.addDevice(WashingMashine.class, "WashingMashine:washingMashine-1", house, new HashMap<String, String>());
		

		final ArrayList<Human.DeviceToken> devices = new ArrayList<Human.DeviceToken>();
		devices.add(new Human.DeviceToken(Lamp.class, lamp1));
		devices.add(new Human.DeviceToken(Lamp.class, lamp2));
		devices.add(new Human.DeviceToken(Lamp.class, lamp3));
		devices.add(new Human.DeviceToken(ElectricKettle.class, kettle));
		devices.add(new Human.DeviceToken(Fridge.class, fridge));
		devices.add(new Human.DeviceToken(MicrowaveOven.class, microwave));
		devices.add(new Human.DeviceToken(ElectricHeater.class, heater));
		devices.add(new Human.DeviceToken(DishWasher.class, dishWasher));
		devices.add(new Human.DeviceToken(MobileDevice.class, mobile));
		devices.add(new Human.DeviceToken(Oven.class, oven));
		devices.add(new Human.DeviceToken(RadioSet.class, radio));
		devices.add(new Human.DeviceToken(VacumCliner.class, vacum));
		devices.add(new Human.DeviceToken(WashingMashine.class, wm));


        devices.add(new Human.DeviceToken(WaterHeater.class, waterHeater));
        devices.add(new Human.DeviceToken(TelevisionSet.class, tv));

		devices.add(new Human.DeviceToken(WaterHeater.class, waterHeater));

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
