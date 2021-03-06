package bgu.spl.mics;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */

public class MessageBusImpl implements MessageBus {
    private static class SingletonHolder {
        private static MessageBusImpl instance = new MessageBusImpl();
    }

    private static ConcurrentHashMap<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>> msPerMessageQ;
    private static HashMap<Event, Future> futurePerEvent;
    private static ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>> messageQs;
    private MessageBusImpl() {
        msPerMessageQ = new ConcurrentHashMap<>();
        futurePerEvent = new HashMap<>();
        messageQs = new ConcurrentHashMap<>();
    }

    public static MessageBusImpl getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * Subscribes microService {@code m} to broadcast/Event message of type {@code type}.
     * <p>
     * 1. If Message {@code type} doesn't exists in MsPerMessageQ, create a new mapping to a new Q.
     * 2. Add the microService {@code m} to the relevant q in the hashMap.
     *
     * Sync will be done:
     * First by the whole msPerMessageQ hash to make sure a few MS won't add the same entity to the hashMap.
     * Then, we'll lock the specific Q in the hashmap to allow subscription to a few different Message types
     * at the same time.
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

   /**
     * Add all the microsevises that support the broadcast {@code b} to the q of the massage b we'll
     * sync first by the specific Q in msPerMessageQ for te Broadcast in order to make sure
     * the list of subscribed MS won't change during the process.
     * Then, we'll sync by the specific Q in order to add the message to the Q and notify all threads
     * waiting to pull from this Q
     *
     * @param b the broadcast to send
     */
    public void sendBroadcast(Broadcast b) {
        if (msPerMessageQ.containsKey(b.getClass())) {
            ConcurrentLinkedQueue<MicroService> mss = msPerMessageQ.get(b.getClass());
            synchronized (mss) {
                for (MicroService m : mss) {
                    ConcurrentLinkedQueue<Message> msQ = messageQs.get(m);
                    synchronized (msQ) {
                        msQ.add(b);
                        msQ.notifyAll();
                    }
                }
            }
        }
    }

    //returns m.s. who are subscribed into specific event - implement roundRubin
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

    /**
     * adds an event massage in each q that subscribes into this specific event after some service sent this event
     * Under getMSForEvent we'll sync first by the specific Q in msPerMessageQ for the event in order to make sure the list
     * of subscribed MS won't change during the process.
     * Then, we'll sync by the specific Q in order to add the message to the Q and notify all threads
     * waiting to pull from this Q.
     * @param e     	The event to add to the queue.
     * @param <T>
     * @return          future object who is the promise result
     */

    public <T> Future<T> sendEvent(Event<T> e) {
        if (!msPerMessageQ.containsKey(e.getClass())) {
            return null;
        } else {
            ConcurrentLinkedQueue<MicroService> mss = msPerMessageQ.get(e.getClass());
            MicroService m1 = getMSForEvent(mss); //Implement roundRubin
            if (m1 == null) return null;
            ConcurrentLinkedQueue<Message> msQ = messageQs.get(m1);
            Future<T> f = null;
            synchronized (msQ) {
                msQ.add(e);
                f = new Future<>();
                futurePerEvent.put(e, f);
                msQ.notifyAll();
            }
            return f;
        }
    }

    /**
     * put the microservice {@code m} in the qs of microservices
     * @param m the microservice to register
     */
    public void register(MicroService m) {
        synchronized (messageQs) { 
            messageQs.put(m, new ConcurrentLinkedQueue<Message>());
        }
    }

   /**
     * As instructed in the office hours and the forum, in order not to harm the the liveness
     * of the program, decided not to support use-cases of unregister happening while
     * a message is being sent to this MS {@code m}. This is why we sync by messageQs for unregister,
     * but sync by Q for send/await message
     *
     * @param m the microservice to unregister
     */
    public void unregister(MicroService m) {
        synchronized (msPerMessageQ) {
            for (Map.Entry<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>> entry : msPerMessageQ.entrySet()) {
                synchronized (entry.getValue()) {
                    entry.getValue().remove(m);
                }
            }
        }
        synchronized (messageQs) {
            messageQs.remove(m);
        }
    }


    /**
     * Pulls a message from the MicroService's Q under messageQs HashMap.
     * Throws exception if no Q was created (under sync of the messageQs hashMap).
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
