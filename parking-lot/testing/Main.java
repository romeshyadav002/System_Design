package testing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Main {
    public enum VehicleType {
        BIKE, CAR, TRUCK
    }

    public static abstract class Vehicle {
        VehicleType vehicleType;
        String licensePlate;

        public Vehicle(VehicleType vehicleType, String licensePlate) {
            this.licensePlate = licensePlate;
            this.vehicleType = vehicleType;
        }

        public String getLicensePlate() {
            return this.licensePlate;
        }

        public VehicleType getVehicleType() {
            return this.vehicleType;
        }
    }

    public static class Bike extends Vehicle {
        public Bike(String licensePlate) {
            super(VehicleType.BIKE, licensePlate);

        }
    }

    public static class Car extends Vehicle {
        public Car(String licensePlate) {
            super(VehicleType.CAR, licensePlate);

        }
    }

    public static class ParkingSpot {
        boolean isAvailable;
        int id;
        VehicleType type;
        Vehicle vehicle;

        ParkingSpot(int id, VehicleType type) {
            this.type = type;
            this.isAvailable = true;
            this.id = id;
        }

        public boolean parkVehicle(Vehicle vehicle) {
            if (!isAvailable || this.type != vehicle.vehicleType) {
                return false;
            }
            this.vehicle = vehicle;
            this.isAvailable = false;
            return true;
        }

        public void unpark() {
            this.vehicle = null;
            this.isAvailable = true;
        }

        public boolean isAvailable() {
            return isAvailable;
        }

        public int getSpotNumber() {
            return id;
        }

        public Vehicle getVehicle() {
            return this.vehicle;
        }
    }

    public static class ParkingFloor {
        int floorNumber;
        List<ParkingSpot> spots;

        ParkingFloor(int floorNumber, List<ParkingSpot> spots) {
            this.floorNumber = floorNumber;
            this.spots = spots;
        }

        public int getFloorNumber() {
            return floorNumber;
        }

        public List<ParkingSpot> geSpots() {
            return this.spots;
        }

        public List<ParkingSpot> getAvailableSpots(VehicleType type) {
            List<ParkingSpot> list = new ArrayList<>();
            for (ParkingSpot spot : spots) {
                if (spot.isAvailable && spot.type == type) {
                    list.add(spot);
                }
            }
            return list;
        }

    }

    public static class Ticket {
        String ticketId;
        Vehicle vehicle;
        ParkingSpot spot;
        long entryTimestamp;
        long exitTimestamp;

        public Ticket(String ticketId, Vehicle vehicle, ParkingSpot spot) {
            this.ticketId = ticketId;
            this.vehicle = vehicle;
            this.spot = spot;
            this.entryTimestamp = new Date().getTime();
        }

        public String getTicketId() {
            return ticketId;
        }

        public Vehicle getVehicle() {
            return vehicle;
        }

        public ParkingSpot getSpot() {
            return spot;
        }

        public long getEntryTimestamp() {
            return entryTimestamp;
        }

        public long getExitTimestamp() {
            return exitTimestamp;
        }

        public void setExitTimestamp() {
            this.exitTimestamp = new Date().getTime();
        }
    }

    public static interface FeeStrategy {
        double CalculateFee(Ticket ticket);
    }

    public static class FlatFeeStrategy implements FeeStrategy {
        private int hourRate = 10;

        public double CalculateFee(Ticket ticket) {
            long duration = ticket.getExitTimestamp() - ticket.getEntryTimestamp();
            long hours = (duration / (1000 * 60 * 60)) + 1;
            return hours * hourRate;
        }
    }

    public static class ParkingLot {
        List<ParkingFloor> floors;
        FeeStrategy feeStrategy;

        ParkingLot() {
            this.floors = new ArrayList<>();
            this.feeStrategy = new FlatFeeStrategy();
        }

        public void addFloor(ParkingFloor floor) {
            floors.add(floor);
        }

        public Ticket parkVehicle(Vehicle vehicle) throws Exception {
            for (ParkingFloor floor : floors) {
                List<ParkingSpot> spots = floor.getAvailableSpots(vehicle.getVehicleType());
                if (spots.size() > 0) {
                    spots.get(0).parkVehicle(vehicle);
                    String ticketId = UUID.randomUUID().toString();
                    Ticket ticket = new Ticket(ticketId, vehicle, spots.get(0));
                    return ticket;
                }
            }
            throw new Exception("No available spots for" + vehicle.getVehicleType());
        }

        public double unparkVehicle(Ticket ticket) throws Exception {
            ParkingSpot spot = ticket.getSpot();
            spot.unpark();
            ticket.setExitTimestamp();
            return feeStrategy.CalculateFee(ticket);
        }
    }

    public static void main(String[] args) {

        ParkingLot parkingLot = new ParkingLot();
        List<ParkingSpot> parkingSpotsFloor1 = new ArrayList<>();
        parkingSpotsFloor1.add(new ParkingSpot(101, VehicleType.BIKE));
        parkingSpotsFloor1.add(new ParkingSpot(102, VehicleType.CAR));

        List<ParkingSpot> parkingSpotsFloor2 = new ArrayList<>();
        parkingSpotsFloor1.add(new ParkingSpot(201, VehicleType.BIKE));
        parkingSpotsFloor1.add(new ParkingSpot(202, VehicleType.CAR));

        ParkingFloor f1 = new ParkingFloor(1, parkingSpotsFloor1);
        ParkingFloor f2 = new ParkingFloor(2, parkingSpotsFloor2);
        parkingLot.addFloor(f1);
        parkingLot.addFloor(f2);

        Vehicle v1 = new Car("123");
        Vehicle v2 = new Bike("1234");
        try {
            Ticket t1 = parkingLot.parkVehicle(v1);
            System.out.println("Car ! parked " + t1.getTicketId());

            double fees = parkingLot.unparkVehicle(t1);
            System.out.println("Car ! Unparked " + fees);
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

}
