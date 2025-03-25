package parking;

import java.util.ArrayList;
import java.util.List;

public class ParkingFloor {
    private int floorNo;
    private List<ParkingSpot> spots;

    public ParkingFloor(int floorNo) {
        this.floorNo = floorNo;
        this.spots = new ArrayList<>();
    }

    public void addParkingSpot(ParkingSpot spot) {
        spots.add(spot);
    }

    public ParkingSpot findAvailableSpot(VehicleType vehicleType) {
        for (ParkingSpot spot : spots) {
            if (spot.isAvailable() && spot.getSpotType() == getRequiredSpotType(vehicleType)) {
                return spot;
            }
        }
        return null;  // No available spot
    }

    private SpotType getRequiredSpotType(VehicleType vehicleType) {
        if (vehicleType == VehicleType.BIKE) return SpotType.COMPACT;
        if (vehicleType == VehicleType.TRUCK) return SpotType.LARGE;
        return SpotType.HANDICAPPED;
    }
}

