package com.chargepoint.fleet;

public class Truck implements Vehicle {
    private int id;
    private int currentCharge;

    private int capacity;

    public Truck(int id, int capacity) {
        this.id = id;
        this.capacity = capacity;
    }

    public Truck(int id, int capacity, int currentCharge) {
        this.id = id;
        this.capacity = capacity;
        this.currentCharge = currentCharge;
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public int currentCharge() {
        return currentCharge;
    }

    public int chargeRemaining() {
        return capacity - currentCharge;
    }
}
