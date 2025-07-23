package Uber;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Main {

    public static enum PaymentStatus {
        PENDING,
        COMPLETED
    }

    public static class User {
        private final String id;
        private final String name;
        private final String contact;

        public User(String name, String contact) {
            this.id = UUID.randomUUID().toString();
            this.name = name;
            this.contact = contact;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getContact() {
            return contact;
        }
    }

    public static enum DriverStatus {
        AVAILABLE, BUSY
    }

    public static class Location {
        private final double latitude;
        private final double longitude;

        public Location(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double distanceTo(Location other) {
            double dx = this.latitude - other.latitude;
            double dy = this.longitude - other.longitude;
            return Math.sqrt(dx * dx + dy * dy); // Euclidean distance
        }
    }

    public static class Driver extends User {
        private final String licensePlate;
        private Location location;
        private DriverStatus status;
        private Trip currentTrip;

        public Driver(String name, String contact, String licensePlate, Location location) {
            super(name, contact);
            this.licensePlate = licensePlate;
            this.location = location;
            this.status = DriverStatus.AVAILABLE;
        }

        public synchronized void updateLocation(Location location) {
            this.location = location;
        }

        public synchronized boolean isAvailable() {
            return status == DriverStatus.AVAILABLE;
        }

        public synchronized void assignTrip(Trip trip) {
            this.currentTrip = trip;
            this.status = DriverStatus.BUSY;
        }

        public synchronized void completeTrip() {
            this.currentTrip = null;
            this.status = DriverStatus.AVAILABLE;
        }

        public Location getLocation() {
            return location;
        }

        public void setStatus(DriverStatus status) {
            this.status = status;
        }

        public String getLicensePlate() {
            return licensePlate;
        }

        public DriverStatus getStatus() {
            return status;
        }

        public Trip getCurrentTrip() {
            return currentTrip;
        }
    }

    public static class Rider extends User {
        private Trip currentTrip;

        public Rider(String name, String contact) {
            super(name, contact);
        }

        public synchronized void assignTrip(Trip trip) {
            this.currentTrip = trip;
        }

        public synchronized void completeTrip() {
            this.currentTrip = null;
        }

        public Trip getCurrentTrip() {
            return currentTrip;
        }
    }

    public static enum TripStatus {
        REQUESTED,
        ACCEPTED,
        ONGOING,
        COMPLETED,
        CANCELLED
    }

    public static class Trip {
        private final String id;
        private Driver driver;
        private final Rider rider;
        private final Location origin;
        private final Location destination;
        private TripStatus status;
        private PaymentStatus paymentStatus;
        private double fare;

        public Trip(Rider rider, Location origin, Location destination) {
            this.id = UUID.randomUUID().toString();
            this.rider = rider;
            this.origin = origin;
            this.destination = destination;
            this.status = TripStatus.REQUESTED;
            this.paymentStatus = PaymentStatus.PENDING;
        }

        public void complete() {
            this.status = TripStatus.COMPLETED;
        }

        public void markPayment() {
            this.paymentStatus = PaymentStatus.COMPLETED;
        }

        public void assignDriver(Driver driver) {
            this.driver = driver;
        }

        public void setStatus(TripStatus status) {
            this.status = status;
        }

        public void setFare(double fare) {
            this.fare = fare;
        }

        public String getId() {
            return id;
        }

        public Rider getRider() {
            return rider;
        }

        public Driver getDriver() {
            return driver;
        }

        public Location getOrigin() {
            return origin;
        }

        public Location getDestination() {
            return destination;
        }

        public TripStatus getStatus() {
            return status;
        }

        public double getFare() {
            return fare;
        }
    }

    public interface Payment {
        void processPayment(double amount);
    }

    public static class UPIPayment implements Payment {
        public void processPayment(double amount) {
            System.out.println("Processing UPI payment of $" + amount);
        }
    }

    public static class RideSharingService {
        private static RideSharingService instance;
        private final Map<String, Driver> drivers;
        private final Map<String, Rider> riders;
        private final Map<String, Trip> trips;

        private RideSharingService() {
            drivers = new ConcurrentHashMap<>();
            riders = new ConcurrentHashMap<>();
            trips = new ConcurrentHashMap<>();
        }

        public static synchronized RideSharingService getInstance() {
            if (instance == null) {
                instance = new RideSharingService();
            }
            return instance;
        }

        public Driver registerDriver(String name, String contact, String licensePlate, Location location) {
            Driver driver = new Driver(name, contact, licensePlate, location);
            drivers.put(driver.getId(), driver);
            return driver;
        }

        public Rider registerRider(String name, String contact) {
            Rider rider = new Rider(name, contact);
            riders.put(rider.getId(), rider);
            return rider;
        }

        public synchronized Trip requestRide(String riderId, Location from, Location to) {
            Rider rider = riders.get(riderId);
            if (rider == null) throw new IllegalArgumentException("Rider not found");

            Trip trip = new Trip(rider, from, to);
            notifyNearbyDrivers(trip);
            trips.put(trip.getId(), trip);
            return trip;
        }

        public void acceptRide(String driverId, String tripId) {
            Driver driver = drivers.get(driverId);
            Trip trip = trips.get(tripId);
            if (trip.getStatus() == TripStatus.REQUESTED) {
                Rider rider = trip.getRider();
                trip.assignDriver(driver);
                trip.setStatus(TripStatus.ACCEPTED);
                driver.assignTrip(trip);
                rider.assignTrip(trip);
                notifyRider(trip);
            }
        }

        public void startRide(String tripId) {
            Trip trip = trips.get(tripId);
            if (trip.getStatus() == TripStatus.ACCEPTED) {
                trip.setStatus(TripStatus.ONGOING);
                notifyRider(trip);
            }
        }

        public synchronized void completeRide(String tripId) {
            Trip trip = trips.get(tripId);
            if (trip.getStatus() == TripStatus.ONGOING) {
                trip.complete();
                trip.getDriver().completeTrip();
                trip.getRider().completeTrip();
                double fare = calculateFare(trip);
                trip.setFare(fare);
                notifyRider(trip);
                notifyDriver(trip);
            }
        }

        public void cancelRide(String tripId) {
            Trip trip = trips.get(tripId);
            if (trip.getStatus() == TripStatus.REQUESTED || trip.getStatus() == TripStatus.ACCEPTED) {
                trip.setStatus(TripStatus.CANCELLED);
                if (trip.getDriver() != null) {
                    trip.getDriver().setStatus(DriverStatus.AVAILABLE);
                }
                notifyDriver(trip);
                notifyRider(trip);
            }
        }

        public void makePayment(String tripId, Payment payment) {
            Trip trip = trips.get(tripId);
            double fare = trip.getFare();
            payment.processPayment(fare);
            trip.markPayment();
        }

        private void notifyNearbyDrivers(Trip trip) {
            boolean driverFound = false;
            for (Driver driver : drivers.values()) {
                if (driver.isAvailable()) {
                    double distance = driver.getLocation().distanceTo(trip.getOrigin());
                    if (distance <= 15.0) {
                        System.out.println("Notifying driver " + driver.getName() + " about trip " + trip.getId());
                        driverFound = true;
                    }
                }
            }
            if (!driverFound) {
                throw new IllegalStateException("No nearby drivers available.");
            }
        }

        private void notifyRider(Trip trip) {
            Rider rider = trip.getRider();
            String message = switch (trip.getStatus()) {
                case ACCEPTED -> "Your ride has been accepted by driver: " + trip.getDriver().getName();
                case ONGOING -> "Your ride is in progress.";
                case COMPLETED -> "Your ride is complete. Fare: $" + trip.getFare();
                case CANCELLED -> "Your ride has been cancelled.";
                default -> "";
            };
            System.out.println("Notifying rider " + rider.getName() + ": " + message);
        }

        private void notifyDriver(Trip trip) {
            Driver driver = trip.getDriver();
            if (driver != null) {
                String message = switch (trip.getStatus()) {
                    case COMPLETED -> "Ride completed. Fare: $" + trip.getFare();
                    case CANCELLED -> "Ride cancelled by rider.";
                    default -> "";
                };
                System.out.println("Notifying driver " + driver.getName() + ": " + message);
            }
        }

        private double calculateFare(Trip trip) {
            double baseFare = 2.0;
            double perKmFare = 1.5;
            double distance = trip.getOrigin().distanceTo(trip.getDestination());
            return Math.round((baseFare + distance * perKmFare) * 100.0) / 100.0;
        }
    }

    public static void main(String[] args) {
        RideSharingService rideSharingService = RideSharingService.getInstance();

        // Register riders
        Rider rider1 = rideSharingService.registerRider("John Doe", "1234567890");
        Rider rider2 = rideSharingService.registerRider("Jane Smith", "9876543210");

        // Register drivers
        Driver driver1 = rideSharingService.registerDriver("Alice Johnson", "4567890123", "ABC123", new Location(37.7749, -122.4194));
        Driver driver2 = rideSharingService.registerDriver("Bob Williams", "7890123456", "XYZ789", new Location(37.7860, -122.4070));

        // Rider1 requests a ride
        Trip trip1 = rideSharingService.requestRide(rider1.getId(), new Location(37.7749, -122.4194), new Location(37.7849, -122.4294));
        rideSharingService.acceptRide(driver1.getId(), trip1.getId());
        rideSharingService.startRide(trip1.getId());
        rideSharingService.completeRide(trip1.getId());
        rideSharingService.makePayment(trip1.getId(), new UPIPayment());

        // Rider2 requests and cancels ride
        Trip trip2 = rideSharingService.requestRide(rider2.getId(), new Location(37.7760, -122.4180), new Location(37.7860, -122.4280));
        rideSharingService.acceptRide(driver2.getId(), trip2.getId());
        rideSharingService.cancelRide(trip2.getId());
    }
}
