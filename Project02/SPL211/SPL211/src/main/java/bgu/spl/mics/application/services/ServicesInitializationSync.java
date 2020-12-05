package bgu.spl.mics.application.services;

import java.util.concurrent.CountDownLatch;

public class ServicesInitializationSync {

    private static CountDownLatch initializationCount=null;
    //numOfServices will mark the amount of services to initialize
    // before an action (Leia's initialization in this case)
    public ServicesInitializationSync(int numOfServices){
        initializationCount = new CountDownLatch(numOfServices);
    }
    public static CountDownLatch getInitializationCount(){
        return initializationCount;
    }
}
