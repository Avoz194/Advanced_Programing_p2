package bgu.spl.mics;


import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
    private static MessageBusImpl instance = null;
    private static ConcurrentHashMap<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>> msPerMessageQ; //TODO: Make sure needs to be concurrent
    private static HashMap<Event, Future> futurePerEvent; //TODO: Make sure if needs to be concurrent
    private static ConcurrentHashMap<MicroService,ConcurrentLinkedQueue<Message>> messageQs; 



    private MessageBusImpl() {
        msPerMessageQ = new ConcurrentHashMap<>();
        futurePerEvent = new HashMap<>();
        messageQs = new ConcurrentHashMap<>();
    }
    
    public static MessageBusImpl getInstance() {
        if (instance == null) { //TODO: sync
            instance = new MessageBusImpl();
        }
        return instance;
    }

    /**
     * Subscribes microService {@code m} to broadcast/Event message of type {@code type}.
     * <p>
     * 1. If Message {@code type} doesn't exists in MsPerMessageQ, create a new mapping to a new Q.
     * 2. Add the microService {@code m} to the relevant q in the hashMap.
     *
     * <p>
     *
     * @param type The {@link Class} representing the type of Message
     *             to subscribe to.
     * @param m    The microService to register to this type of Message.
     */
    private void addToMsPerMessageQ(Class<? extends Message> type, MicroService m) { //TODO: figure out whether to sync
        if (!msPerMessageQ.containsKey(type))
            msPerMessageQ.put(type, new ConcurrentLinkedQueue<>());
        msPerMessageQ.get(type).add(m);
    }

    /**
     * Subscribes microService {@code m} to Event message of type {@code type}.
     * Use the private method @addToMsPerMessageQ, which:
     * 1. If Message {@code type} doesn't exists in MsPerMessageQ, create a new mapping to a new Q.
     * 2. Add the microService {@code m} to the relevant q in the hashMap.
     *
     * <p>
     *
     * @param type The {@link Class} representing the type of Event
     *             to subscribe to.
     * @param m    The microService to register to this type of Event.
     */
    //Use private function addToMsPerMessageQ.
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        addToMsPerMessageQ(type, m);
    }

    /**
     * Subscribes microService {@code m} to Broadcast message of type {@code type}.
     * Use the private method @addToMsPerMessageQ, which:
     * 1. If Message {@code type} doesn't exists in MsPerMessageQ, create a new mapping to a new Q.
     * 2. Add the microService {@code m} to the relevant q in the hashMap.
     *
     * <p>
     *
     * @param type The {@link Class} representing the type of Broadcast
     *             to subscribe to.
     * @param m    The microService to register to this type of Broadcast.
     */
    //Use private function addToMsPerMessageQ.
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        addToMsPerMessageQ(type, m);
    }


    /**
     * Completes the received request {@code e} with the result {@code result}
     * using Future's resolve.
     * Look for the matching future for this event using futurePerEvent HashMap.
     *
     * <p>
     *
     * @param <T>    The type of the expected result of the processed event
     *               {@code e}.
     * @param e      The event to complete.
     * @param result The result to resolve the relevant Future object.
     *               {@code e}.
     */
    public <T> void complete(Event<T> e, T result) {
        if (!futurePerEvent.containsKey(e)) {
            throw new RuntimeException("Every Event should first be sent and matched to a Future object");
        } else {
            Future<T> f = futurePerEvent.get(e);

            if (f.isDone())
                throw new RuntimeException("Can't resolve an already resolved future");
            else
                f.resolve(result); //TODO:Resolve should be synced
        }
    }

    @Override
    public void sendBroadcast(Broadcast b) {
        if(!msPerMessageQ.containsValue(b)){
            throw new RuntimeException("a event that hasn't been registered cant be broadcast");
        }
        ConcurrentLinkedQueue<MicroService> mss = msPerMessageQ.get(b);
        for (MicroService m : mss) {
            messageQs.get(m).add(b);
        }
    }


    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        if(!msPerMessageQ.containsValue(e)){
            throw new RuntimeException("an event that hasn't been registered cant be sent");
        }
        ConcurrentLinkedQueue<MicroService> mss = msPerMessageQ.get(e);
        MicroService m1 = mss.poll();
        messageQs.get(m1).add(e);
        mss.add(m1);
        Future<T> f = new Future<>();
        futurePerEvent.put(e,f);
        return f;
    }

    @Override
    public void register(MicroService m) {

    }

    @Override
    public void unregister(MicroService m) {

    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {

        return null;
    }

}
