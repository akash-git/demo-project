package com.chargepoint.utils;

import com.chargepoint.chargers.Charger;
import com.chargepoint.fleet.Vehicle;

import java.util.List;
import java.util.Map;

public class PrintSchedule {
    public static void printSchedule(Map<Charger, List<Vehicle>> schedule) {
        System.out.println("Schedule");

        for (Charger charger : schedule.keySet()) {
            System.out.print(charger.getID() + ":");
            for (Vehicle vehicle : schedule.get(charger)) {
                System.out.print(vehicle.getID() + ", ");
            }

            System.out.println("");
        }

    }
}
