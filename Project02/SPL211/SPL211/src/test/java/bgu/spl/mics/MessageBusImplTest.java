package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.VictoryBroadcast;
import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {

    private MessageBusImpl mb;
    private MicroService ms1;
    private MicroService ms2;


    @BeforeEach
    public void SetUp() {
        mb = MessageBusImpl.getInstance();
        ms1 = new HanSoloMicroservice();
        mb.register(ms1);
        ms2 = new C3POMicroservice();
        mb.register(ms2);
    }

    @AfterEach
    public void tearDown() {
        mb.unregister(ms1);
        mb.unregister(ms2);
    }

    /*Test Case (Test sendEvent and awaitMessage - same flow):
     *Assuming ms1 and ms2 are registered.
     * Subscribe ms1 to AttackEvents with empty callback, and ms2 to send an event
     * ms1 pulls the message from Q
     * Make sure the event was inserted to ms1's Q and not to ms3's
     */
    @Test
    public void testSendEvent() throws InterruptedException {
        AttackEvent e1 = new AttackEvent();
        HanSoloMicroservice ms3 = new HanSoloMicroservice();
        mb.register(ms3);
        mb.subscribeEvent(AttackEvent.class, ms1);
        ms2.sendEvent(e1);
        assertTrue(e1.equals(mb.awaitMessage(ms1)));
        assertFalse(e1.equals(mb.awaitMessage(ms3)));

    }

    /* Identical Test Case to the above, this time for sendBroadcast()
     */
    @Test
    public void testSendBroadcast() throws InterruptedException {
        VictoryBroadcast b1 = new VictoryBroadcast();
        mb.subscribeBroadcast(VictoryBroadcast.class, ms1);
        HanSoloMicroservice ms3 = new HanSoloMicroservice();
        mb.register(ms3);
        mb.subscribeBroadcast(VictoryBroadcast.class, ms3);
        ms2.sendBroadcast(b1);
        assertTrue(b1.equals(mb.awaitMessage(ms1)));
        assertTrue(b1.equals(mb.awaitMessage(ms3)));
        assertFalse(b1.equals(mb.awaitMessage(ms2)));

    }

    /*TestCase:
     * Assuming the microservice already pulled the Message e1 and has the matching future f1.
     * Complete event e1.
     */
    @Test
    public void testComplete() {
        AttackEvent e1 = new AttackEvent();
        mb.subscribeEvent(AttackEvent.class, ms1);
        Future f1 = mb.sendEvent(e1);
        assertFalse(f1.isDone());
        mb.complete(e1, true);
        assertTrue(f1.isDone());
        assertEquals(true, f1.get());
    }

}