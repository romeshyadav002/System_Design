package parking;

import java.util.ArrayList;
import java.util.List;

public class ParkingLot {
    private List<ParkingFloor> floors;
    private List<Gate> gates;

    public ParkingLot() {
        this.floors = new ArrayList<>();
        this.gates = new ArrayList<>();
    }

    public void addFloor(ParkingFloor floor) {
        floors.add(floor);
    }

    public void addGate(Gate gate) {
        gates.add(gate);
    }

    public ParkingTicket allocateSpot(Vehicle vehicle) {
        for (ParkingFloor floor : floors) {
            ParkingSpot availableSpot = floor.findAvailableSpot(vehicle.getVehicleType());
            if (availableSpot != null) {
                availableSpot.assignVehicle();
                return new ParkingTicket(vehicle, availableSpot);
            }
        }
        System.out.println("No available spot for vehicle type: " + vehicle.getVehicleType());
        return null;
    }

    public void releaseSpot(ParkingTicket ticket) {
        ticket.getSpotAssigned().freeSpot();
    }
}
