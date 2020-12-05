package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;

import java.util.concurrent.CountDownLatch;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {

    private CountDownLatch LeiaReadyToStart=null;

    public R2D2Microservice(long duration) {
        super("R2D2");
        this.LeiaReadyToStart = LeiaReadyToStart;
    }

    @Override
    protected void initialize() {

        LeiaReadyToStart.countDown(); //Signal he finished initializing
    }
}
