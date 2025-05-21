package elevator;

import java.util.*;

enum Direction { UP, DOWN, IDLE }
enum LiftStatus { MOVING, IDLE }
enum RequestType { EXTERNAL, INTERNAL }

class Floor {
    int floorNumber;
    public Floor(int floorNumber) { this.floorNumber = floorNumber; }
    public int getFloorNumber() { return floorNumber; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Floor floor = (Floor) o;
        return floorNumber == floor.floorNumber;
    }
    @Override
    public int hashCode() {
        return Objects.hash(floorNumber);
    }
}

class Request {
    Floor floor;
    Direction direction;
    RequestType requestType;
    public Request(Floor floor, Direction direction, RequestType requestType) {
        this.floor = floor;
        this.direction = direction;
        this.requestType = requestType;
    }
    public Floor getFloor() { return floor; }
    public Direction getDirection() { return direction; }
    public RequestType getRequestType() { return requestType; }
}

class LiftDisplay {
    int liftId;

    public LiftDisplay(int liftId) {
        this.liftId = liftId;
    }

    public void display(Floor floor, Direction direction, LiftStatus status) {
        System.out.println("Lift " + liftId + " - Floor: " + floor.getFloorNumber() + ", Direction: " + direction + ", Status: " + status);
    }
}

class InternalButton {
    Floor floor;
    ElevatorController controller;
    int liftId;

    public InternalButton(Floor floor, ElevatorController controller, int liftId) {
        this.floor = floor;
        this.controller = controller;
        this.liftId = liftId;
    }
    public void pressButton() {
        controller.addRequest(new Request(floor, Direction.IDLE, RequestType.INTERNAL));
    }
    public Floor getFloor() { return floor; }
}

class ExternalButton {
    Floor floor;
    Direction direction;
    ElevatorController controller;
    public ExternalButton(Floor floor, Direction direction, ElevatorController controller) {
        this.floor = floor;
        this.direction = direction;
        this.controller = controller;
    }
    public void pressButton() {
        controller.addRequest(new Request(floor, direction, RequestType.EXTERNAL));
    }
}

class Lift {
    int liftId;
    Floor currentFloor;
    Direction direction;
    LiftStatus status;
    List<InternalButton> internalButtons = new ArrayList<>();
    LiftDisplay liftDisplay;
    List<Floor> destinationFloors = new ArrayList<>();

    public Lift(int liftId, Floor currentFloor, ElevatorController controller, int numFloors) {
        this.liftId = liftId;
        this.currentFloor = currentFloor;
        this.direction = Direction.IDLE;
        this.status = LiftStatus.IDLE;
        this.liftDisplay = new LiftDisplay(liftId);
        for (int i = 0; i < numFloors; i++) {
            internalButtons.add(new InternalButton(new Floor(i), controller, liftId));
        }
    }
    public void moveUp() { direction = Direction.UP; status = LiftStatus.MOVING; }
    public void moveDown() { direction = Direction.DOWN; status = LiftStatus.MOVING; }
    public void stop() { direction = Direction.IDLE; status = LiftStatus.IDLE; }
    public void setCurrentFloor(Floor floor) {
        this.currentFloor = floor;
        liftDisplay.display(this.currentFloor, this.direction, this.status);
    }
    public Floor getCurrentFloor() { return currentFloor; }
    public Direction getDirection() { return direction; }
    public LiftStatus getStatus() { return status; }
    public int getLiftId() { return liftId; }
    public void addDestinationFloor(Floor floor){
        destinationFloors.add(floor);
    }
    public void removeDestinationFloor(Floor floor){
        destinationFloors.remove(floor);
    }
    public List<Floor> getDestinationFloors(){
        return destinationFloors;
    }
}

class ElevatorController {
    List<Lift> lifts = new ArrayList<>();
    List<Request> requests = new ArrayList<>();
    int numFloors;

    public ElevatorController(int numLifts, int numFloors) {
        this.numFloors = numFloors;
        for (int i = 0; i < numLifts; i++) {
            lifts.add(new Lift(i, new Floor(0), this, numFloors));
        }
    }

    public void addRequest(Request request) {
        requests.add(request);
        controlLifts();
    }

    public void controlLifts() {
        lookAlgorithm();
    }

    private Lift findBestLift(Request request) {
        Lift bestLift = null;
        int minDistance = Integer.MAX_VALUE;

        for (Lift lift : lifts) {
            if (lift.getStatus() == LiftStatus.IDLE) {
                int distance = Math.abs(lift.getCurrentFloor().getFloorNumber() - request.getFloor().getFloorNumber());
                if (distance < minDistance) {
                    minDistance = distance;
                    bestLift = lift;
                }
            } else if (lift.getDirection() == request.getDirection() &&
                       ((lift.getDirection() == Direction.UP && lift.getCurrentFloor().getFloorNumber() <= request.getFloor().getFloorNumber()) ||
                        (lift.getDirection() == Direction.DOWN && lift.getCurrentFloor().getFloorNumber() >= request.getFloor().getFloorNumber()))) {
                int distance = Math.abs(lift.getCurrentFloor().getFloorNumber() - request.getFloor().getFloorNumber());
                if (distance < minDistance) {
                    minDistance = distance;
                    bestLift = lift;
                }
            }
        }
        if(bestLift == null){
            bestLift = lifts.get(0);
        }
        return bestLift;
    }

    private void lookAlgorithm() {
        for (Lift lift : lifts) {
            if (lift.getStatus() == LiftStatus.IDLE && !requests.isEmpty()) {
                Request bestRequest = requests.remove(0);
                Lift bestLift = findBestLift(bestRequest);
                bestLift.addDestinationFloor(bestRequest.getFloor());
                if (bestLift.getCurrentFloor().getFloorNumber() < bestRequest.getFloor().getFloorNumber()) {
                    bestLift.moveUp();
                } else if (bestLift.getCurrentFloor().getFloorNumber() > bestRequest.getFloor().getFloorNumber()) {
                    bestLift.moveDown();
                }
                moveLift(bestLift);
            } else if (lift.getStatus() == LiftStatus.MOVING) {
                moveLift(lift);
            }
        }
    }

    private void moveLift(Lift lift) {
        if (lift.getDestinationFloors().isEmpty()) {
            lift.stop();
            return;
        }

        Floor nextDestination = lift.getDestinationFloors().get(0);
        if (lift.getCurrentFloor().getFloorNumber() < nextDestination.getFloorNumber()) {
            lift.moveUp();
            lift.setCurrentFloor(new Floor(lift.getCurrentFloor().getFloorNumber() + 1));
        } else if (lift.getCurrentFloor().getFloorNumber() > nextDestination.getFloorNumber()) {
            lift.moveDown();
            lift.setCurrentFloor(new Floor(lift.getCurrentFloor().getFloorNumber() - 1));
        }

        if (lift.getCurrentFloor().equals(nextDestination)) {
            lift.removeDestinationFloor(nextDestination);
            lift.stop();
        }
    }
}

class Building {
    ElevatorController controller;
    List<Floor> floors = new ArrayList<>();

    public Building(int numFloors, int numLifts) {
        this.controller = new ElevatorController(numLifts, numFloors);
        for (int i = 0; i < numFloors; i++) {
            floors.add(new Floor(i));
        }
        new ExternalButtonDispatcher(controller, numFloors);
    }

    public ElevatorController getController() {
        return controller;
    }

    public Floor getFloor(int floorNumber) {
        for (Floor floor : floors) {
            if (floor.getFloorNumber() == floorNumber) {
                return floor;
            }
        }
        return null;
    }
}

class ExternalButtonDispatcher {
    public ExternalButtonDispatcher(ElevatorController controller, int numFloors) {
        for (int i = 0; i < numFloors; i++) {
            new ExternalButton(new Floor(i), Direction.UP, controller);
            new ExternalButton(new Floor(i), Direction.DOWN, controller);
        }
    }
}

class InternalButtonDispatcher {
    public InternalButtonDispatcher(ElevatorController controller, Lift lift, int numFloors) {
        for (int i = 0; i < numFloors; i++) {
            lift.internalButtons.get(i).pressButton();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Building building = new Building(10, 2);
        ElevatorController controller = building.getController();

        controller.addRequest(new Request(building.getFloor(5), Direction.UP, RequestType.EXTERNAL));
        controller.addRequest(new Request(building.getFloor(8), Direction.DOWN, RequestType.EXTERNAL));
        controller.addRequest(new Request(building.getFloor(2), Direction.UP, RequestType.EXTERNAL));

        controller.lifts.get(0).internalButtons.get(9).pressButton();

        for (int i = 0; i < 20; i++) {
            controller.controlLifts();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}