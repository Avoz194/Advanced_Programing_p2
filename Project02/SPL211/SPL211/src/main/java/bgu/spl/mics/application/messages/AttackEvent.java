package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

import java.util.List;

public class AttackEvent implements Event<Boolean> {

    private List<Integer> serials;
    private int duration;

    public AttackEvent(List<Integer> ser, int dur) {
        serials = ser;
        duration = dur;
    }

}
