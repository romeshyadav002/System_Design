package parking;

public class Main {
    public static void main(String[] args) {
        ParkingLot parkingLot = new ParkingLot();
        ParkingFloor floor1 = new ParkingFloor(1);

        // Adding parking spots to floor
        floor1.addParkingSpot(new ParkingSpot(1, SpotType.COMPACT));
        floor1.addParkingSpot(new ParkingSpot(2, SpotType.LARGE));
        floor1.addParkingSpot(new ParkingSpot(3, SpotType.HANDICAPPED));
        parkingLot.addFloor(floor1);

        // Adding gates to parking lot
        EntryGate entryGate1 = new EntryGate(1, parkingLot);
        ExitGate exitGate1 = new ExitGate(2);

        // Vehicle entering
        Vehicle car = new Vehicle("KA-01-1234", VehicleType.CAR);
        ParkingTicket ticket = entryGate1.processEntry(car);

        if (ticket != null) {
            // Simulate parking time with sleep (optional)
            try {
                Thread.sleep(2000);  // Simulate parking for a few seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Vehicle exiting
            exitGate1.processExit(ticket);
        }
    }
}

