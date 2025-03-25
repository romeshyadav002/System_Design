package parking;

import java.time.LocalDateTime;

public class ParkingTicket {
    private static int ticketCounter = 1;
    private int ticketId;
    private Vehicle vehicle;
    private LocalDateTime entryTime;
    private ParkingSpot spotAssigned;

    public ParkingTicket(Vehicle vehicle, ParkingSpot spotAssigned) {
        this.ticketId = ticketCounter++;
        this.vehicle = vehicle;
        this.entryTime = LocalDateTime.now();
        this.spotAssigned = spotAssigned;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public ParkingSpot getSpotAssigned() {
        return spotAssigned;
    }

    public int getTicketId() {
        return ticketId;
    }
}

