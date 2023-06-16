import com.chargepoint.chargers.Charger;
import com.chargepoint.chargers.GenericCharger;
import com.chargepoint.fleet.Truck;
import com.chargepoint.fleet.Vehicle;
import com.chargepoint.scheduler.BasicScheduler;
import com.chargepoint.utils.PrintSchedule;
import com.chargepoint.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScheduleTest {


    public static void main(String[] args) {
        List<Vehicle> vehicles = getVehicles();
        List<Charger> chargers = getChargers();
        int time = 2;

        Scheduler scheduler = new BasicScheduler();

        Map<Charger, List<Vehicle>> schedule = scheduler.schedule(vehicles, chargers, time);

        PrintSchedule.printSchedule(schedule);
    }

    static List<Charger> getChargers() {
        List<Charger> chargers = new ArrayList<Charger>();
        chargers.add(new GenericCharger(1, 5));
        chargers.add(new GenericCharger(2, 3));
        return chargers;
    }

    static List<Vehicle> getVehicles() {
        List<Vehicle> vehicles = new ArrayList<Vehicle>();
        vehicles.add(new Truck(1, 10, 2));
        vehicles.add(new Truck(2, 10, 4));
        vehicles.add(new Truck(3, 9, 4));
        vehicles.add(new Truck(4, 10, 5));

        return vehicles;
    }
}
