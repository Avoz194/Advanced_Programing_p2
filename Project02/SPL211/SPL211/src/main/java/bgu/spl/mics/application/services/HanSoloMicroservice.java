package bgu.spl.mics.application.services;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;

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

    private CountDownLatch LeiaReadyToStart=null;

    public HanSoloMicroservice(){super("Han");} //Empty Constructor for tests
    public HanSoloMicroservice(CountDownLatch LeiaReadyToStart) {
        super("Han");
        this.LeiaReadyToStart = LeiaReadyToStart;
    }


    @Override
    protected void initialize() {

        LeiaReadyToStart.countDown(); //Signal he finished initializing
    }
}
