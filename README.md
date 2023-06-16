assumptions
    time required to charge vehicle = vehicle charge capacity / charger capacity 

algo
    start with lowest vehicle charge required

        find charger with minimum diff capacity >= charge required * remaining time for charger
            - first try to select lower charge rate charger
            - if not found, select higher charge rate charger
        
        if no charger found, return false
        else, reduce assignable time to charger

        if assignable time == 0 remove charger from chargers list

        remove vehicle from vehicles list
        if no charger remaining for chargerate, remove set from map
        
        repeat until no vehicles left
    
    return schedule

coding 
 - print few logs, so to trace flow
 - BasicScheduler, contians all logic within using inner classes

Class structure
    ScheduleTest - basic test for scheduler
    Vehicle(Interface)
        - Truck
    Charger(Interface)
        - GenericCharger
    Scheduler(Interface)
        - BasicScheduler
          - Contains inner classes, 
            - VehicleSelector
            - ChargerSelector(Interface)
              - Multiple implemenration
                - BasicChargerSelector
                  - Using basic priority queue
                - ChargerSelectorMap
                  - Using treemap and set
                    - TreeMap helps finding closest chargeRate charger
                    - Set helps with minimum duration left, so we can densely allocate chargers

    

    
