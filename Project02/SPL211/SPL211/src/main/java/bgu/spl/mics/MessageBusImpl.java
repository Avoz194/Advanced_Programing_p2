package bgu.spl.mics;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
    private static MessageBusImpl instance = null;
    private static HashMap<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>> msPerMessageQ; //TODO: Make sure needs to be concurrent
    private static HashMap<Event, Future> futurePerEvent;
    private static HashMap<MicroService, ConcurrentLinkedQueue<Message>> messageQs;//TODO: Make sure needs to be concurrent


    private MessageBusImpl() {
        msPerMessageQ = new HashMap<>();
        futurePerEvent = new HashMap<>();
        messageQs = new HashMap<>();
    }

    public static MessageBusImpl getInstance() {
        synchronized (instance) {  //TODO:revise
            if (instance == null) {
                instance = new MessageBusImpl();
            }
            return instance;
        }
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
    private void addToMsPerMessageQ(Class<? extends Message> type, MicroService m) {
        synchronized (msPerMessageQ) {
            if (!msPerMessageQ.containsKey(type))
                msPerMessageQ.put(type, new ConcurrentLinkedQueue<>());
        }
        synchronized (msPerMessageQ.get(type)) {
            msPerMessageQ.get(type).add(m);
        }
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
                f.resolve(result);
        }
    }

    public void sendBroadcast(Broadcast b) {
        if (msPerMessageQ.containsValue(b)) {

            ConcurrentLinkedQueue<MicroService> mss = msPerMessageQ.get(b);
            synchronized (mss) {
                for (MicroService m : mss) {
                    ConcurrentLinkedQueue<Message> msQ = messageQs.get(m);
                    synchronized (msQ) {
                        msQ.add(b);
                        notifyAll();
                    }
                }
            }
        }
    }

    private MicroService getMSForEvent(ConcurrentLinkedQueue<MicroService> qOfMS) {
        synchronized (qOfMS) {
            if (qOfMS.isEmpty()) {
                return null;
            }
            MicroService m1 = qOfMS.poll();
            qOfMS.add(m1);
            return m1;
        }
    }

    public <T> Future<T> sendEvent(Event<T> e) {
        if (!msPerMessageQ.containsValue(e)) {
            return null;
        } else {
            ConcurrentLinkedQueue<MicroService> mss = msPerMessageQ.get(e);
            MicroService m1 = getMSForEvent(mss); //TODO:make sure works
            if (m1 == null) return null;
            ConcurrentLinkedQueue<Message> msQ = messageQs.get(m1);
            synchronized (msQ) {
                msQ.add(e);
                notifyAll();
            }
            Future<T> f = new Future<>();
            futurePerEvent.put(e, f);
            return f;
        }
    }

    public void register(MicroService m) {
        synchronized (messageQs) { //TODO: consider adding a counter of how many Q's are blocked in order to block messageQ as a whole
            messageQs.put(m, new ConcurrentLinkedQueue<Message>());
        }
    }

    public void unregister(MicroService m) {
        synchronized (messageQs) {
            messageQs.remove(m);
        }
        synchronized (msPerMessageQ) {
            for (Map.Entry<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>> entry : msPerMessageQ.entrySet()) {
                msPerMessageQ.remove(entry.getKey(), m);
            }
        }
    }


    /**
     * Pulls a message from the MicroService's Q under messageQs HashMap.
     * Throws exception if no Q was created.
     * In case the Q is empty, wait until a message enters (notifyAll sent from @sendEvent and @sendBroadcast functions)
     * <p>
     * Return a message
     * <p>
     *
     * @param m The microService asks to pull an event from it's Q
     */
    public Message awaitMessage(MicroService m) throws InterruptedException {
        synchronized (messageQs) {
            if (!messageQs.containsKey(m)) {
                throw new RuntimeException("MicroService must be registered before pulling a message");
            }
        }
        ConcurrentLinkedQueue<Message> msQ = messageQs.get(m);
        synchronized (msQ) {
            while (msQ.isEmpty()) {
                try {
                    msQ.wait();
                } catch (InterruptedException e) {
                }
            }
            return msQ.poll();
        }
    }

}
