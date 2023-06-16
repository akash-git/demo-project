package com.chargepoint.chargers;

public class GenericCharger implements Charger{
    private int id;
    private int chargeRate;

    public GenericCharger(int id, int chargeRate) {
        this.id = id;
        this.chargeRate = chargeRate;
    }

    public int getID() {
        return id;
    }

    public int chargeRate() {
        return chargeRate;
    }
}
