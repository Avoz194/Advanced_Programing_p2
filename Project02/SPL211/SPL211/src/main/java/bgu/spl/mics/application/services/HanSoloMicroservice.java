package bgu.spl.mics.application.services;


import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {

    private CountDownLatch initializationCount = null;

    public HanSoloMicroservice() {
        super("Han");
    } //Empty Constructor for tests

    public void setInitializationCount(CountDownLatch initializationCount) {
        this.initializationCount = initializationCount;
    }

    @Override
    protected void initialize() {
        subscribeEvent(AttackEvent.class, (AttackEvent event) -> {
            int[] ewoks = array(event.getSerial());
            long duration = event.getDuration();
            Ewoks.getInstance().acquire(ewoks);
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {

            }
            Ewoks.getInstance().release(ewoks);
            Diary.getInstance().incrementTotalAttacks();
            complete(event, true);
        });
        subscribeBroadcast(VictoryBroadcast.class, (VictoryBroadcast broad) -> {
            Timestamp time = new Timestamp(System.currentTimeMillis());
            Diary.getInstance().setHanSoloTerminate(time.getTime());
            complete(broad, true);
        });
        subscribeBroadcast(NoMoreAttacksBroadcast.class, (NoMoreAttacksBroadcast broad) -> {
            Timestamp time = new Timestamp(System.currentTimeMillis());
            Diary.getInstance().setHanSoloFinish(time.getTime());
            complete(broad, true);
        });


        initializationCount.countDown(); //Signal he finished initializing
    }

    private int[] array(List<Integer> l) {
        int[] ans = new int[l.size()];
        for (int i = 0; i < l.size(); i++) {
            ans[i] = l.get(i);
        }
        return ans;
    }
}

