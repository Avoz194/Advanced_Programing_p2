package bgu.spl.mics.application.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.messages.*;



/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
    private Attack[] attacks;
    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
        this.attacks = attacks;
    }

    protected void initialize() {
        subscribeBroadcast(VictoryBroadcast.class, (param) -> {
            Timestamp time = new Timestamp(System.currentTimeMillis());
            Diary.getInstance().setLeiaTerminate(time.getTime());
        });
        manageAttacks();
        manageDeactivation();
        manageBobmDestroyer();
    }
    private void manageAttacks(){
        ArrayList<Future> futuresToFollow = new ArrayList<>();
        for(Attack at:attacks){
            AttackEvent e = new AttackEvent(at.getSerials(), at.getDuration());
            futuresToFollow.add(sendEvent(e));
        }
        sendBroadcast(new NoMoreAttacksBroadcast());
        for(Future f:futuresToFollow){
            while (!f.isDone()){ //In order not to cast (although the result is always boolean in our flow), we use isDone function.
                try{
                    f.get();
                } catch (InterruptedException e){}
            }
        }
    }
    private void manageDeactivation(){
        Future f = sendEvent(new DeactivationEvent());
        while (!f.isDone()){ //In order not to cast (although the result is always boolean in our flow), we use isDone function.
            try{
                f.get();
            } catch (InterruptedException e){}
        }
    }
    private void manageBobmDestroyer(){
        Future f = sendEvent(new BombDestroyerEvent());
        while (!f.isDone()){ //In order not to cast (although the result is always boolean in our flow), we use isDone function.
            try{
                f.get();
            } catch (InterruptedException e){}
        }
        sendBroadcast(new VictoryBroadcast());
    }
}
