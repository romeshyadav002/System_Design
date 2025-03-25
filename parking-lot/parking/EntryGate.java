package parking;

public class EntryGate extends Gate {

    private ParkingLot parkingLot;

    public EntryGate(int gateId, ParkingLot parkingLot) {
        super(gateId, GateType.ENTRY);
        this.parkingLot = parkingLot;
    }

    public ParkingTicket processEntry(Vehicle vehicle) {
        System.out.println("Vehicle " + vehicle.getVehicleNumber() + " is entering through Entry Gate " + getGateId());
        ParkingTicket ticket = parkingLot.allocateSpot(vehicle);

        if (ticket != null) {
            System.out.println("Parking Spot assigned. Ticket ID: " + ticket.getTicketId());
            return ticket;
        } else {
            System.out.println("No parking spots available for vehicle: " + vehicle.getVehicleNumber());
            return null;
        }
    }
}
