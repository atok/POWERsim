package agh.powerSim.simulation.actors.humans;

public class HumanCharacter {//characteristics
    int lightComfortTreshold = 100; //sum of lightbulbs power
    int lightOverloadTreshold = 150; //max 
    int entertainment;
    int hungerConfortTreshold = 1400; //kalorie
    int hunger=hungerConfortTreshold;
    int workDuty;
    int cleaner;
    double warmComfortTreshold = 16.0;
    double overheatComfortTreshold = 26.0;

    public HumanCharacter() {
    }

    public int getLightComfortTreshold() {
        return lightComfortTreshold;
    }

    public void setLightComfortTreshold(int lightComfortTreshold) {
        this.lightComfortTreshold = lightComfortTreshold;
    }

    public int getEntertainment() {
        return entertainment;
    }

    public void setEntertainment(int entertainment) {
        this.entertainment = entertainment;
    }

    public int getHunger() {
        return hunger;
    }

    public void setHunger(int hunger) {
        this.hunger = hunger;
    }

    public int getWorkDuty() {
        return workDuty;
    }

    public void setWorkDuty(int workDuty) {
        this.workDuty = workDuty;
    }

    public int getCleaner() {
        return cleaner;
    }

    public void setCleaner(int cleaner) {
        this.cleaner = cleaner;
    }

    public double getWarmComfortTreshold() {
        return warmComfortTreshold;
    }

    public void setWarmComfortTreshold(double warmComfortTreshold) {
        this.warmComfortTreshold = warmComfortTreshold;
    }
}