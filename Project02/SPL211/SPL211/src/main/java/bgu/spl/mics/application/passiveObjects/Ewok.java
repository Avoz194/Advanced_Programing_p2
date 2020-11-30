package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a forest creature summoned when HanSolo and C3PO receive AttackEvents.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Ewok {
    int serialNumber;
    boolean available;

    public Ewok(int serialNumber) {
        if (serialNumber < 0) {
            throw new IllegalArgumentException("index of ewok must be non-negative int");
        }
        this.serialNumber = serialNumber;
        this.available = true;
    }

    public boolean getAvailable() {
        return available;
    }

    /**
     * Acquires an Ewok
     */
    public void acquire() {
        if (!available) { //TODO: change to sync
            throw new IllegalArgumentException("you can't acquire an ewok that as been allready acquired.");
        } else {
            this.available = true;
        }
    }

    /**
     * release an Ewok
     */
    public void release() {
        if (available) {
            throw new IllegalArgumentException("you can't release an ewok that hasnt been acquired yet");
        } else {
            this.available = false;
        }
    }
}

