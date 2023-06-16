package com.chargepoint.scheduler;

import com.chargepoint.chargers.Charger;
import com.chargepoint.fleet.Vehicle;

import java.util.List;
import java.util.Map;

public interface Scheduler {
    public Map<Charger, List<Vehicle>> schedule(List<Vehicle> list, List<Charger> chargers, int time);

}
