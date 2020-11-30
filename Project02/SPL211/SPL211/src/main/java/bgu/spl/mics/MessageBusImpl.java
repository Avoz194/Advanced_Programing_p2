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

    private MessageBusImpl() {
        msPerMessageQ = new ConcurrentHashMap<>();
    }

    public static MessageBusImpl getInstance() {
        if (instance == null) {
            instance = new MessageBusImpl();
        }
        return instance;
    }

    /**
     * Subscribes microService {@code m} to broadcast/Event message of type {@code type}.
     *
     * 1. If Message {@code type} doesn't exists in MsPerMessageQ, create a new mapping to a new Q.
     * 2. Add the microService {@code m} to the relevant q in the hashMap.
     *
     * @param type     The {@link Class} representing the type of Message
     *                 to subscribe to.
     * @param m        The microService to register to this type of Message.
     */
    private void addToMsPerMessageQ(Class<? extends Message> type, MicroService m) { //TODO: figure out whether to sync
        if (!msPerMessageQ.containsKey(type))
            msPerMessageQ.put(type, new ConcurrentLinkedQueue<>());
        msPerMessageQ.get(type).add(m);
    }

    @Override
    //Use private function addToMsPerMessageQ.
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        addToMsPerMessageQ(type, m);
    }

    //Use private function addToMsPerMessageQ.
    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        addToMsPerMessageQ(type, m);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void complete(Event<T> e, T result) {

    }

    @Override
    public void sendBroadcast(Broadcast b) {

    }


    @Override
    public <T> Future<T> sendEvent(Event<T> e) {

        return null;
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
