+-----------------------------------------+
|             ParkingLot                  |
+-----------------------------------------+
| - name: String                          |
| - address: String                       |
| - parkingFloors: List<ParkingFloor>     |
| - entryGates: List<EntryGate>           |
| - exitGates: List<ExitGate>             |
+-----------------------------------------+
| + addParkingFloor(ParkingFloor): void   |
| + getAvailableSpot(VehicleType): Spot   |
| + parkVehicle(Vehicle): ParkingTicket   |
| + removeVehicle(ParkingTicket): double  |
+-----------------------------------------+

                1       *                  
                |-----------------
                |                |
 +-----------------------+   +-----------------------+
 |       EntryGate       |   |       ExitGate        |
 +-----------------------+   +-----------------------+
 | - gateId: int         |   | - gateId: int         |
 | - isOpen: boolean     |   | - isOpen: boolean     |
 | - operatorName: String|   | - operatorName: String|
 +-----------------------+   +-----------------------+
 | + scanVehicle(Vehicle):  |   + scanTicket(ticket)  |
 |   ParkingTicket         |   + processPayment()     |
 +-----------------------+   +-----------------------+

                1       *                  
                |----------------
                |               |
+----------------------------------+
|          ParkingFloor            |
+----------------------------------+
| - floorNumber: int               |
| - spots: List<ParkingSpot>       |
+----------------------------------+
| + getAvailableSpot(): Spot       |
| + addParkingSpot(Spot): void     |
+----------------------------------+

               1        *                  
               |------------------
               |                 |
+-----------------------------+     +-------------------------+
|       ParkingSpot           |     |       Vehicle           |
+-----------------------------+     +-------------------------+
| - spotId: int               |     | - licensePlate: String  |
| - isOccupied: boolean       |     | - type: VehicleType     |
| - spotType: SpotType        |     +-------------------------+
+-----------------------------+

               1        *                  
               |------------------
               |                 |
+-------------------------------+
|      ParkingTicket            |
+-------------------------------+
| - ticketId: int               |
| - entryTime: LocalDateTime    |
| - exitTime: LocalDateTime     |
| - parkingSpot: ParkingSpot    |
| - vehicle: Vehicle            |
+-------------------------------+

---

### **Key Relationships:**
1. **ParkingLot → ParkingFloor**: A ParkingLot has multiple floors.
2. **ParkingFloor → ParkingSpot**: Each floor has multiple parking spots.
3. **ParkingSpot → Vehicle**: A parking spot can be occupied by one vehicle at a time.
4. **ParkingLot → EntryGate & ExitGate**: ParkingLot has multiple entry and exit gates.
5. **EntryGate → ParkingTicket**: EntryGate issues parking tickets.
6. **ExitGate → ParkingTicket**: ExitGate processes the parking tickets to calculate time-based charges.

---

### **Enums:**
To handle the types of vehicles and spots:
1. **VehicleType Enum**:
   ```java
   public enum VehicleType {
       CAR, BIKE, TRUCK;
   }
