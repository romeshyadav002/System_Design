package AirLineManagementSystem;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static class Passenger {
        String id;
        String name;
        String email;

        Passenger(String name, String email) {
            this.email = email;
            this.name = name;
            this.id = UUID.randomUUID().toString();
        }

        public String getId() {
            return this.id;
        }
    }

    public static class Aircraft {
        String tailNo;
        String modelNo;
        int seats;

        Aircraft(String tailNo, String modelNo, int seats) {
            this.tailNo = tailNo;
            this.modelNo = modelNo;
            this.seats = seats;
        }

        public int getSeats() {
            return this.seats;
        }

        public String getTailNumber() {
            return tailNo;
        }
    }

    public static enum SeatStatus {
        AVAILABLE,
        RESERVED,
        OCCUPIED
    }

    public static class Seat {
        String seatNo;
        SeatStatus status;

        Seat(String seatNo) {
            this.seatNo = seatNo;
            status = SeatStatus.AVAILABLE;
        }

        public String getSeatNumber() {
            return seatNo;
        }

        public void reserve() {
            status = SeatStatus.RESERVED;
        }

        public void release() {
            status = SeatStatus.AVAILABLE;
        }

        public synchronized boolean isBooked() {
            return status == SeatStatus.OCCUPIED;
        }
    }

    public static class Flight {
        private final String flightNumber;
        private final String source;
        private final String destination;
        private final LocalDateTime departureTime;
        private final LocalDateTime arrivalTime;
        private final Aircraft aircraft;
        private final Map<String, Seat> seats;
        private final List<Seat> availableSeats;

        public Flight(String source, String destination, LocalDateTime departureTime, LocalDateTime arrivalTime, Aircraft aircraft) {
            this.flightNumber = UUID.randomUUID().toString();
            this.source = source;
            this.destination = destination;
            this.departureTime = departureTime;
            this.arrivalTime = arrivalTime;
            this.aircraft = aircraft;
            this.seats = new HashMap<>();
            this.availableSeats = new ArrayList<>();

            int rows = aircraft.getSeats() / 6;
            char[] seatCols = {'A', 'B', 'C', 'D', 'E', 'F'};

            for (int i = 1; i <= rows; i++) {
                for (char col : seatCols) {
                    String seatNo = i + String.valueOf(col);
                    Seat seat = new Seat(seatNo);
                    seats.put(seatNo, seat);
                    availableSeats.add(seat);
                }
            }
        }

        public synchronized boolean isSeatAvailable(String seatNo) {
            Seat seat = seats.get(seatNo);
            return seat != null && !seat.isBooked();
        }

        public synchronized void reserveSeat(String seatNo) {
            Seat seat = seats.get(seatNo);
            if (seat == null) throw new IllegalArgumentException("Invalid seat number");
            seat.reserve();
        }

        public synchronized void releaseSeat(String seatNo) {
            Seat seat = seats.get(seatNo);
            if (seat != null) seat.release();
        }

        public String getSource() { return source; }
        public String getDestination() { return destination; }
        public LocalDateTime getDepartureTime() { return departureTime; }
        public String getFlightNumber() { return flightNumber; }
        public LocalDateTime getArrivalTime() { return arrivalTime; }
        public List<Seat> getAvailableSeats() { return availableSeats; }
        public Map<String, Seat> getSeats() { return seats; }
    }

    public static class FlightSearch {
        private final List<Flight> flights;

        public FlightSearch() {
            this.flights = new ArrayList<>();
        }

        public void addFlight(Flight flight) {
            flights.add(flight);
        }

        public List<Flight> searchFlights(String source, String destination, LocalDate date) {
            return flights.stream()
                    .filter(flight -> flight.getSource().equalsIgnoreCase(source)
                            && flight.getDestination().equalsIgnoreCase(destination)
                            && flight.getDepartureTime().toLocalDate().equals(date))
                    .collect(Collectors.toList());
        }
    }

    public enum PaymentStatus {
        PENDING, COMPLETED, FAILED, REFUNDED
    }

    public static class Payment {
        private final String paymentId;
        private final String paymentMethod;
        private final double amount;
        private PaymentStatus status;

        public Payment(String paymentId, String paymentMethod, double amount) {
            this.paymentId = paymentId;
            this.paymentMethod = paymentMethod;
            this.amount = amount;
            this.status = PaymentStatus.PENDING;
        }

        public void processPayment() {
            status = PaymentStatus.COMPLETED;
        }
    }

    public static class PaymentProcessor {
        private static PaymentProcessor instance;

        private PaymentProcessor() {}

        public static synchronized PaymentProcessor getInstance() {
            if (instance == null) {
                instance = new PaymentProcessor();
            }
            return instance;
        }

        public void processPayment(Payment payment) {
            payment.processPayment();
        }
    }

    public enum BookingStatus {
        CONFIRMED, CANCELLED, PENDING, EXPIRED
    }

    public static class Booking {
        private final String id;
        private final Flight flight;
        private final Passenger passenger;
        private final Seat seat;
        private final double price;
        private BookingStatus status;

        public Booking(Flight flight, Passenger passenger, Seat seat, double price) {
            this.id = UUID.randomUUID().toString();
            this.flight = flight;
            this.passenger = passenger;
            this.seat = seat;
            this.price = price;
            this.status = BookingStatus.CONFIRMED;
        }

        public void cancel() {
            status = BookingStatus.CANCELLED;
            seat.release();
        }

        public String getId() {
            return id;
        }
    }

    public static class BookingManager {
        private static BookingManager instance;
        private final Map<String, Booking> bookings;
        private final Object lock = new Object();

        private BookingManager() {
            bookings = new HashMap<>();
        }

        public static synchronized BookingManager getInstance() {
            if (instance == null) {
                instance = new BookingManager();
            }
            return instance;
        }

        public Booking createBooking(Flight flight, Passenger passenger, Seat seat, double price) {
            String bookingNumber = UUID.randomUUID().toString();
            Booking booking = new Booking(flight, passenger, seat, price);
            synchronized (lock) {
                bookings.put(bookingNumber, booking);
            }
            return booking;
        }

        public void cancelBooking(String bookingNumber) {
            synchronized (lock) {
                Booking booking = bookings.get(bookingNumber);
                if (booking != null) booking.cancel();
            }
        }
    }

    public static class AirlineManagementSystem {
        private final Map<String, Flight> flights;
        private final Map<String, Aircraft> aircrafts;
        private final Map<String, Passenger> passengers;
        private final FlightSearch flightSearch;
        private final BookingManager bookingManager;
        private final PaymentProcessor paymentProcessor;

        public AirlineManagementSystem() {
            flights = new HashMap<>();
            aircrafts = new HashMap<>();
            passengers = new HashMap<>();
            flightSearch = new FlightSearch();
            bookingManager = BookingManager.getInstance();
            paymentProcessor = PaymentProcessor.getInstance();
        }

        public Passenger addPassenger(String name, String email) {
            Passenger passenger = new Passenger(name, email);
            passengers.put(passenger.getId(), passenger);
            return passenger;
        }

        public Aircraft addAircraft(String tailNumber, String model, int totalSeats) {
            Aircraft aircraft = new Aircraft(tailNumber, model, totalSeats);
            aircrafts.put(tailNumber, aircraft);
            return aircraft;
        }

        public Flight addFlight(String source, String destination, LocalDateTime departure, LocalDateTime arrival, String aircraftNumber) {
            Aircraft aircraft = aircrafts.get(aircraftNumber);
            Flight flight = new Flight(source, destination, departure, arrival, aircraft);
            flights.put(flight.getFlightNumber(), flight);
            flightSearch.addFlight(flight);
            return flight;
        }

        public List<Flight> searchFlights(String source, String destination, LocalDate date) {
            return flightSearch.searchFlights(source, destination, date);
        }

        public Booking bookFlight(String flightNumber, String passengerId, Seat seat, double price) {
            Flight flight = flights.get(flightNumber);
            Passenger passenger = passengers.get(passengerId);
            return bookingManager.createBooking(flight, passenger, seat, price);
        }

        public void cancelBooking(String bookingNumber) {
            bookingManager.cancelBooking(bookingNumber);
        }

        public void processPayment(Payment payment) {
            paymentProcessor.processPayment(payment);
        }
    }

    public static void main(String[] args) {
        AirlineManagementSystem airlineManagementSystem = new AirlineManagementSystem();

        Passenger passenger1 = airlineManagementSystem.addPassenger("John Doe", "john@example.com");
        Passenger passenger2 = airlineManagementSystem.addPassenger("John Smith", "smith@example.com");

        Aircraft aircraft1 = airlineManagementSystem.addAircraft("A001", "Boeing 747", 300);
        Aircraft aircraft2 = airlineManagementSystem.addAircraft("A002", "Airbus A380", 500);

        LocalDateTime departureTime1 = LocalDateTime.now().plusDays(1);
        LocalDateTime arrivalTime1 = departureTime1.plusHours(2);
        Flight flight1 = airlineManagementSystem.addFlight("New York", "London", departureTime1, arrivalTime1, aircraft1.getTailNumber());

        LocalDateTime departureTime2 = LocalDateTime.now().plusDays(3);
        LocalDateTime arrivalTime2 = departureTime2.plusHours(5);
        Flight flight2 = airlineManagementSystem.addFlight("Paris", "Tokyo", departureTime2, arrivalTime2, aircraft2.getTailNumber());

        List<Flight> searchResults = airlineManagementSystem.searchFlights("New York", "London", LocalDate.now().plusDays(1));
        System.out.println("Search Results:");
        for (Flight flight : searchResults) {
            System.out.println("Flight: " + flight.getFlightNumber() + " - " + flight.getSource() + " to " + flight.getDestination());
        }

        Seat seatToBook = flight1.getAvailableSeats().get(0);
        Booking booking = airlineManagementSystem.bookFlight(flight1.getFlightNumber(), passenger1.getId(), seatToBook, 100);
        if (booking != null) {
            System.out.println("Booking successful. Booking ID: " + booking.getId());
        } else {
            System.out.println("Booking failed.");
        }

        airlineManagementSystem.cancelBooking(booking.getId());
        System.out.println("Booking cancelled.");
    }
}
