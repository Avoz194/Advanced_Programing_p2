package bgu.spl.mics.application;

import java.util.concurrent.CountDownLatch;

/**
 * This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
    public static void main(String[] args) {
        //TODO: don't forget to create CountDownLatch with (4) and build each MS with it. Make sure synchronized object
        CountDownLatch initializationCount = new CountDownLatch(4);

    }
}

