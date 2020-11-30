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
    private Ewoks(int numOfEwoks) {
        this.ewokVector = new Vector<Ewok>(numOfEwoks); // vector of ewoks
    }

    public static Ewoks getInstance(int numOfEwoks) { // singleton instance checker  //TODO: make sure structure of Singelton is Correct
        if (instance == null) {
            instance = new Ewoks(numOfEwoks);
        }
        return instance;
    }
    public static Ewoks getInstance() { // singleton instance checker
        if (instance == null) {
            throw new NoSuchElementException("Ewoks should be initizalized first with the numOfEwoks in the program");
        }
        return instance;
    }

    public void acquire(int [] ewoks) {
//        if (!(e.getAvailable()) || ewokVector.contains(e)) { //TODO: Replace with Sync
//            throw new IllegalArgumentException("you can't acquire an ewok that as been allready acquired.");
//        }
        for (int i = 0;i<ewoks.length; i++){
            ewokVector.elementAt(ewoks[i]).acquire();
        }


    }

    public void release(int [] ewoks) {
//        if ((e.getAvailable()) || !(ewokVector.contains(e))) { //TODO: Replace with Sync
//            throw new IllegalArgumentException("you can't release an ewok that hasnt been acquired yet");

        for (int i = 0;i<ewoks.length; i++){
            ewokVector.elementAt(ewoks[i]).release();
        }

    }
}
