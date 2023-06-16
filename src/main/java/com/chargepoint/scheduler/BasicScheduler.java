package com.chargepoint.scheduler;

import com.chargepoint.chargers.Charger;
import com.chargepoint.fleet.Vehicle;

import java.util.*;

public class BasicScheduler implements Scheduler {
    public Map<Charger, List<Vehicle>> schedule(List<Vehicle> vehicles, List<Charger> chargers, int time) {
        VehicleSelector vehicleSelector = new VehicleSelector(vehicles);
        ChargerSelector chargerSelector = getChargerSelector(chargers, time);

        return chargerAllocated(vehicleSelector, chargerSelector);
    }

    private Map<Charger, List<Vehicle>> chargerAllocated(VehicleSelector vehicleSelector, ChargerSelector chargerSelector) {
        Map<Charger, List<Vehicle>> schedule = new HashMap<>();

        while (vehicleSelector.hasNextVehicle()) {
            Vehicle vehicle = vehicleSelector.getNextVehicle();
            Charger charger = getAssignedCharger(vehicle, chargerSelector);

            if (charger == null) {
                // no charger available
                break;
            }

            schedule.putIfAbsent(charger, new ArrayList<>());

            schedule.get(charger).add(vehicle);
        }

        return schedule;
    }

    Charger getAssignedCharger(Vehicle vehicle, ChargerSelector chargerSelector) {

        System.out.println("vehicle = " + vehicle.getID() + "  - " + vehicle.chargeRemaining());

        int chargeRequired  = vehicle.chargeRemaining();
        ChargerAssigner chargerAssigner = chargerSelector.getSuitableCharger(chargeRequired);

        if (chargerAssigner == null) {
            System.out.println("No charger available");
            return null;
        }

        chargerSelector.assignCharger(chargerAssigner, chargeRequired);

        return chargerAssigner.getCharger();
    }

    private ChargerSelector getChargerSelector(List<Charger> chargers, int time) {
        return new ChargerSelectorMap(chargers, time);
    }

    interface ChargerSelector {
        ChargerAssigner getSuitableCharger(int chargeRequired);
        void assignCharger(ChargerAssigner chargerAssigner, int chargeRequired);
    }

    private class ChargerSelectorMap implements ChargerSelector {
        TreeMap<Integer, TreeSet<ChargerAssigner>> map;

        int time = 0;
        ChargerSelectorMap() {
            map = new TreeMap<>();
        }
        ChargerSelectorMap(List<Charger> chargers, int time) {
            this();
            sort(chargers, time);
        }

        private void sort(List<Charger> chargers, int time) {
            this.time = time;
            for (Charger charger : chargers) {
                setTreeSetIfRequired(charger.chargeRate());
                map.get(charger.chargeRate()).add(new ChargerAssigner(charger, time));
            }

//            System.out.println("map = " + map.get(3));
        }

        void setTreeSetIfRequired(int chargeRate) {
            if (!map.containsKey(chargeRate)) {
                map.put(chargeRate, new TreeSet<>(
                        (o1, o2) -> o1.getAllocatedTime() - o2.getAllocatedTime()
                ));
            }
        }

        public ChargerAssigner getSuitableCharger(int chargeRequired) {
            ChargerAssigner charger = getSuitableCharger(chargeRequired, true);

            if (charger != null) {
                return charger;
            }

            if (map.size() == 0) {
                return null;
            }
            return getSuitableCharger(map.firstKey(), false);
        }

        ChargerAssigner getSuitableCharger(int chargeRequired, boolean lower) {
            System.out.println("chargeRequired = " + chargeRequired);
            Integer chargeRate = getKey(chargeRequired, lower);
            Integer prevChargeRate = -1;

            while (chargeRate != null) {
                if (prevChargeRate == chargeRate) {
                    break;
                }
                prevChargeRate = chargeRate;
                System.out.println("chargeRate = " + chargeRate);
                TreeSet<ChargerAssigner> chargers = map.get(chargeRate);

                if (chargers.isEmpty()) {
                    System.out.println("empty set for = " + chargeRate);
                    chargeRate = getKey(chargeRequired, lower);
                    continue;
                }

                ChargerAssigner charger = chargers.first();

                System.out.println("charger = " + charger);

                if (charger.isAvailable(chargeRequired)) {
                    return charger;
                }

                chargeRate = getKey(chargeRequired, lower);

            }

            return null;

        }

        Integer getKey(Integer chargeRequired, boolean lower) {
            try {
                if (lower) {
                    return map.floorKey(chargeRequired);
                } else {
                    return map.ceilingKey(chargeRequired);
                }
            }
            catch (Exception e) {
                return null;
            }
        }

        public void assignCharger(ChargerAssigner chargerAssigner, int chargeRequired) {
            int chargeRate = chargerAssigner.getCharger().chargeRate();
            int timeRequired = chargeRequired / chargeRate;
            chargerAssigner.allotactedCharger(timeRequired);

            TreeSet<ChargerAssigner> set = map.get(chargeRate);
            set.remove(chargerAssigner);

            if (chargerAssigner.getAllocatedTime() > 0) {
                set.add(chargerAssigner);
            }
            else {
                if (set.isEmpty()) {
                    map.remove(chargeRate);
                }
            }
        }

    }

    private class BasicChargerSelector implements ChargerSelector{
        PriorityQueue<ChargerAssigner> pq;

        BasicChargerSelector() {
            pq = getPQ();
        }

        BasicChargerSelector(List<Charger> chargers, int time) {
            this(chargers);
        }
        BasicChargerSelector(List<Charger> chargers) {
            this();
            sort(chargers);
        }

        private void sort(List<Charger> chargers) {
            for (Charger charger : chargers) {
                pq.add(new ChargerAssigner(charger));
            }
        }

        private PriorityQueue<ChargerAssigner> getPQ() {
            PriorityQueue<ChargerAssigner> pq = new PriorityQueue<ChargerAssigner>((a, b) -> {
                // need to check if this is correct
                return a.getCharger().chargeRate() - b.getCharger().chargeRate();
            });

            return pq;
        }

        public ChargerAssigner getSuitableCharger(int chargeRequired) {
            return pq.poll();
        }

        public void assignCharger(ChargerAssigner chargerAssigner, int chargeRequired) {
            int chargeRate = chargerAssigner.getCharger().chargeRate();
            int timeRequired = chargeRequired / chargeRate;
            chargerAssigner.allotactedCharger(timeRequired);
        }

    }

    private class ChargerAssigner {
        private Charger charger;

        private int allocated = 0;

        ChargerAssigner(Charger charger) {
            this.charger = charger;
        }

        ChargerAssigner(Charger charger, int duration) {
            allocated = duration;
            this.charger = charger;
        }

        void allotactedCharger(int duration) {
            // check for -vie duration
            allocated -= duration;
        }

        int getAllocatedTime() {
            return allocated;
        }

        Charger getCharger() {
            return charger;
        }

        boolean isAvailable(int chargeRequired) {
            return allocated * charger.chargeRate() >= chargeRequired;
        }

        @Override
        public String toString() {
            return "{id: " + charger.getID() + ", chargeRate: " + charger.chargeRate() + ", allocated: " + allocated + "}";
        }
    }

    private class VehicleSelector {
        PriorityQueue<Vehicle> pq;

        VehicleSelector(List<Vehicle> vehicles) {
            pq = getPQ();
            setPQ(vehicles);
        }

        boolean hasNextVehicle() {
            return !pq.isEmpty();
        }

        Vehicle getNextVehicle() {
            return pq.poll();
        }
        private PriorityQueue<Vehicle> getPQ() {
            PriorityQueue<Vehicle> pq = new PriorityQueue<Vehicle>((a, b) -> {
                int aCharge = a.capacity() - a.currentCharge();
                int bCharge = b.capacity() - b.currentCharge();
                return aCharge - bCharge;
            });

            return pq;
        }

        void setPQ(List<Vehicle> vehicles) {
            for (Vehicle vehicle : vehicles) {
                pq.add(vehicle);
            }
        }

    }
}
