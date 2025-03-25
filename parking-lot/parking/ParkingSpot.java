package parking;

public class ParkingSpot {
    private int spotId;
    private SpotType spotType;
    private boolean isAvailable;

    public ParkingSpot(int spotId, SpotType spotType) {
        this.spotId = spotId;
        this.spotType = spotType;
        this.isAvailable = true;  // Initially, spot is available
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void assignVehicle() {
        this.isAvailable = false;
    }

    public void freeSpot() {
        this.isAvailable = true;
    }

    public SpotType getSpotType() {
        return spotType;
    }
}

enum SpotType {
    COMPACT, LARGE, HANDICAPPED
}

