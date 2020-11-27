package bgu.spl.mics.application.passiveObjects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;


import static org.junit.jupiter.api.Assertions.*;

class EwokTest {
    private Ewok ewok;

    @BeforeEach
    public void setUp() throws Exception {
        ewok = new Ewok(1);
    }
    /*Test flow:
    *
    * *
    */

    @Test
    public void testGetAvailable() {
        assertTrue(ewok.getAvailable());
    }
    @Test
    public void testAcquire() {
        assertTrue(ewok.getAvailable());
        ewok.acquire();
        assertFalse(ewok.getAvailable());
    }

    @Test
    public void testRelease() {
        assertFalse(ewok.getAvailable());
        ewok.release();
        assertTrue(ewok.getAvailable());
    }
}
