package bgu.spl.mics.application.services;

import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;

public class ServicesInitializationSync {

    private static ServicesInitializationSync instance=null;
    private CountDownLatch initializationCount=null;
    //numOfServices will mark the amount of services to initialize
    // before an action (Leia's initialization in this case)
    private ServicesInitializationSync(int numOfServices){
        initializationCount = new CountDownLatch(numOfServices);
    }
    public static ServicesInitializationSync getInstance(int numOfServices){
        if(instance==null){
            instance= new ServicesInitializationSync(numOfServices);
        }
        return instance;
    }
    public static ServicesInitializationSync getInstance(){
        if(instance==null){
            throw new NoSuchElementException("ServicesInitializationSync should be initizalized first with the numOfServices in the program");
        }
        return instance;
    }
    public CountDownLatch getInitializationCount(){
        return initializationCount;
    }
}
