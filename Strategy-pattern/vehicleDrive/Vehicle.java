public class Vehicle {
    DriveStrategy driveObj;
    public Vehicle(DriveStrategy driveObj){
        this.driveObj = driveObj;
    }
    public void drive(){
        driveObj.drive();
    }
}
