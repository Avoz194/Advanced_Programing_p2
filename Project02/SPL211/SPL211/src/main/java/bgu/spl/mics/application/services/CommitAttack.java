package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.util.List;

public class CommitAttack implements Callback {
    private final Callback<AttackEvent> attackCallback  = new Callback<AttackEvent>(){

        @Override
        public void call(AttackEvent event) {
            int[] ewoks = array(event.getSerial());
            long duration = event.getDuration();
            Ewoks.getInstance().acquire(ewoks);
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {}
            Ewoks.getInstance().release(ewoks);
            Diary.getInstance().incrementTotalAttacks();
        }
    };

    private int[] array(List<Integer> l) {
        int[] ans = new int[l.size()];
        for (int i = 0; i < l.size(); i++) {
            ans[i] = l.get(i);
        }
        return ans;
    }

    @Override
    public void call(Object c) {

    }
}
