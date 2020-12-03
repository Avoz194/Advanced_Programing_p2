package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Diary;

import bgu.spl.mics.application.messages.*;

import java.sql.Time;
import java.sql.Timestamp;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice extends MicroService {
    private long duration;

    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration = duration;
    }

    protected void initialize() {
        subscribeEvent(BombDestroyerEvent.class, (BombDestroyerEvent event) -> {
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
            }
            complete(event, true);
        });
        subscribeBroadcast(VictoryBroadcast.class, (param) -> {
            Timestamp time = new Timestamp(System.currentTimeMillis());
            Diary.getInstance().setLandoTerminate(time.getTime());
        });
    }
}
