package com.chargepoint.fleet;

public interface Vehicle {

    int getID();

    int capacity();
    int currentCharge();

    int chargeRemaining();

}
