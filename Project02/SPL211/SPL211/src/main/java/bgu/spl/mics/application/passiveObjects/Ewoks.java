package bgu.spl.mics.application.passiveObjects;

import java.util.*;


/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {
    private Vector<Ewok> ewokVector; //vector because it is tread safe
    private static Ewoks instance = null; //  ewok is a singleton

    // private constructor
    private Ewoks() {
        this.ewokVector = new Vector<Ewok>(1); // vector of ewoks
    }

    public static Ewoks getInstance() { // singleton instance checker
        if (instance == null) {
            instance = new Ewoks();
        }
        return instance;
    }

    public void acquire(int ewok) {
        Ewok e = new Ewok(ewok);
        if (!(e.getAvailable()) || ewokVector.contains(e)) {
            throw new IllegalArgumentException("you can't acquire an ewok that as been allready acquired.");
        } else {
            ewokVector.insertElementAt(e, ewok);
            e.acquire();
        }
    }

    public void release(int ewok) {
        Ewok e = new Ewok(ewok);
        if ((e.getAvailable()) || !(ewokVector.contains(e))) {
            throw new IllegalArgumentException("you can't release an ewok that hasnt been acquired yet");
        } else {
            ewokVector.remove(ewok);
            e.release();
        }
    }
}
