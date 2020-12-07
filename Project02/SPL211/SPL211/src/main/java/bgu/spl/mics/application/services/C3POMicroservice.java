package bgu.spl.mics.application.services;

import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.MicroService;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.CountDownLatch;


/**
 * C3POMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends MicroService {

    private CountDownLatch initializationCount = null;

    public C3POMicroservice() {
        super("C3PO");
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
            Diary.getInstance().setC3POTerminate(time.getTime());
        });
        subscribeBroadcast(NoMoreAttacksBroadcast.class, (NoMoreAttacksBroadcast broad) -> {
            Timestamp time = new Timestamp(System.currentTimeMillis());
            Diary.getInstance().setC3POFinish(time.getTime());
        });


        initializationCount.countDown(); //Signal he finished initializing
    }

    private int[] array(List<Integer> l) {
        int[] ans = new int[l.size()];
        for (Integer i:l) {
            ans[i] = l.get(i);
        }
        return ans;
    }
}

