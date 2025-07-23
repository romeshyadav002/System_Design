package Testing;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static enum Direction {
        UP, DOWN
    }

    public static class Elevator implements Runnable {
        private final int id;
        private final int capacity;
        private int currentFloor;
        private Direction direction;
        private final List<Request> requests;

        public Elevator(int id, int capacity) {
            this.id = id;
            this.capacity = capacity;
            this.currentFloor = 0;
            this.direction = Direction.UP;
            this.requests = new ArrayList<>();
        }

        public synchronized void addRequest(Request request) {
            if (requests.size() < capacity) {
                requests.add(request);
                System.out.println("Elevator " + id + " added request: " + request.getSourceFloor() + " -> " + request.getDestinationFloor());
                notify(); // Wake up thread if waiting
            }
        }

        public synchronized Request getNextRequest() {
            while (requests.isEmpty()) {
                try {
                    wait(); // Wait for requests
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
            return requests.remove(0);
        }

        public void processRequest(Request request) {
            int startFloor = this.currentFloor;
            int endFloor = request.getDestinationFloor();

            // Move to source floor first
            if (startFloor != request.getSourceFloor()) {
                moveToFloor(request.getSourceFloor());
            }

            // Then move to destination
            moveToFloor(endFloor);
        }

        private void moveToFloor(int targetFloor) {
            int step = currentFloor < targetFloor ? 1 : -1;
            direction = step == 1 ? Direction.UP : Direction.DOWN;

            while (currentFloor != targetFloor) {
                currentFloor += step;
                System.out.println("Elevator " + id + " reached floor " + currentFloor);
                try {
                    Thread.sleep(300); // Simulate movement
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        public void run() {
            while (true) {
                Request request = getNextRequest();
                if (request != null) {
                    processRequest(request);
                }
            }
        }

        public int getCurrentFloor() {
            return currentFloor;
        }

        public Direction getDirection() {
            return direction;
        }
    }

    public static class Request {
        private final int sourceFloor;
        private final int destinationFloor;

        public Request(int sourceFloor, int destinationFloor) {
            this.sourceFloor = sourceFloor;
            this.destinationFloor = destinationFloor;
        }

        public int getSourceFloor() {
            return sourceFloor;
        }

        public int getDestinationFloor() {
            return destinationFloor;
        }
    }

    public static class ElevatorController {
        private final List<Elevator> elevators;

        public ElevatorController(int numElevators, int capacity) {
            elevators = new ArrayList<>();
            for (int i = 0; i < numElevators; i++) {
                Elevator elevator = new Elevator(i + 1, capacity);
                elevators.add(elevator);
                new Thread(elevator).start(); // Start processing
            }
        }

        public void requestElevator(int src, int dst) {
            Elevator optimalElevator = findOptimalElevator(src, dst);
            optimalElevator.addRequest(new Request(src, dst));
        }

        public Elevator findOptimalElevator(int src, int dst) {
            Elevator optimalElevator = null;
            int minDistance = Integer.MAX_VALUE;
            Direction userDirection = (src - dst) > 0 ? Direction.DOWN : Direction.UP;

            List<Elevator> optimalElevators = elevators.stream().anyMatch(e -> e.getDirection() == userDirection)
                    ? elevators.stream().filter(e -> e.getDirection() == userDirection).toList()
                    : elevators;

            for (Elevator elevator : optimalElevators) {
                int distance = Math.abs(src - elevator.getCurrentFloor());
                if (distance < minDistance) {
                    minDistance = distance;
                    optimalElevator = elevator;
                }
            }

            return optimalElevator;
        }
    }

    public static void main(String[] args) {
        ElevatorController controller = new ElevatorController(3, 5);

        controller.requestElevator(5, 10);
        controller.requestElevator(3, 7);
        controller.requestElevator(8, 2);
        controller.requestElevator(1, 9);
    }
}
