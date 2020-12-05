package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Diary;

import bgu.spl.mics.application.messages.*;

import java.sql.Timestamp;
import java.util.concurrent.CountDownLatch;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice extends MicroService {
    private long duration;
    private CountDownLatch initializationCount = null;

    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration = duration;
        this.initializationCount=ServicesInitializationSync.getInitializationCount();
    }

    protected void initialize() {
        subscribeEvent(BombDestroyerEvent.class, (BombDestroyerEvent event) -> {
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
            }
            complete(event, true);
        });
        subscribeBroadcast(VictoryBroadcast.class, (VictoryBroadcast broad) -> {
            Timestamp time = new Timestamp(System.currentTimeMillis());
            Diary.getInstance().setLandoTerminate(time.getTime());
        });
        initializationCount.countDown(); //Signal he finished initializing
    }
}
