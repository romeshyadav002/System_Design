package parking;

import java.time.LocalDateTime;

public class ExitGate extends Gate {

    public ExitGate(int gateId) {
        super(gateId, GateType.EXIT);
    }

    public void processExit(ParkingTicket ticket) {
        System.out.println("Vehicle " + ticket.getVehicle().getVehicleNumber() + " is exiting through Exit Gate " + getGateId());

        double billAmount = BillingSystem.calculateBill(ticket);
        System.out.println("Total bill: $" + billAmount);

        ticket.getSpotAssigned().freeSpot();  // Free up the parking spot
        System.out.println("Parking spot freed for future use.");
    }
}
