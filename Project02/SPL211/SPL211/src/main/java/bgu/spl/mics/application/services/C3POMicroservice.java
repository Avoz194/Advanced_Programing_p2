package bgu.spl.mics.application.services;

import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.MicroService;

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

    private CountDownLatch LeiaReadyToStart;

    public C3POMicroservice(CountDownLatch LeiaReadyToStart) {
        super("C3PO");
        this.LeiaReadyToStart = LeiaReadyToStart;
    }

    @Override
    protected void initialize() {


        LeiaReadyToStart.countDown(); //Signal he finished initializing
    }
}
