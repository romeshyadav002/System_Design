package parking;

public abstract class Gate {
    private int gateId;
    private GateType gateType;

    public Gate(int gateId, GateType gateType) {
        this.gateId = gateId;
        this.gateType = gateType;
    }

    public int getGateId() {
        return gateId;
    }

    public GateType getGateType() {
        return gateType;
    }
}

enum GateType {
    ENTRY, EXIT
}

