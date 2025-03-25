package parking;

import java.time.Duration;
import java.time.LocalDateTime;

public class BillingSystem {
    private static final double BASE_RATE_PER_HOUR = 20.0;

    public static double calculateBill(ParkingTicket ticket) {
        LocalDateTime entryTime = ticket.getEntryTime();
        LocalDateTime exitTime = LocalDateTime.now();
        long minutesParked = Duration.between(entryTime, exitTime).toMinutes();

        if (minutesParked <= 60) {
            return BASE_RATE_PER_HOUR;  // Base charge for first hour
        }

        // Calculate per-hour charge after first hour
        double hoursParked = Math.ceil((double) minutesParked / 60);
        return hoursParked * BASE_RATE_PER_HOUR;
    }
}

