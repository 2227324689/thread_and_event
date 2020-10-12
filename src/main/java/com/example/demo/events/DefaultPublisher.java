package com.example.demo.events;

import com.example.demo.events.listener.Subscriber;
import com.example.demo.utils.ConcurrentHashSet;
import com.example.demo.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import static com.example.demo.events.NotifyCenter.ringBufferSize;

/**
 * 咕泡学院，只为更好的你
 * 咕泡学院-Mic: 2082233439
 * http://www.gupaoedu.com
 **/
@Slf4j
public class DefaultPublisher extends Thread implements EventPublisher{
    private Class<? extends Event> eventType;
    private int queueMaxSize = -1;
    private BlockingQueue<Event> queue;
    private volatile boolean initialized = false;
    protected final ConcurrentHashSet<Subscriber> subscribers = new ConcurrentHashSet<Subscriber>();
    private final AtomicReferenceFieldUpdater<DefaultPublisher, Long> updater = AtomicReferenceFieldUpdater
            .newUpdater(DefaultPublisher.class, Long.class, "lastEventSequence");
    private volatile boolean shutdown = false;
    protected volatile Long lastEventSequence = -1L;

    @Override
    public void init(Class<? extends Event> type, int bufferSize) {
        setDaemon(true);
        setName("GuPao.publisher-" + type.getName());
        this.eventType = type;
        this.queueMaxSize = bufferSize;
        this.queue = new ArrayBlockingQueue<Event>(bufferSize);
        start();
        System.out.println("NotifyCenter init success");
    }

    @Override
    public synchronized void start() {
        if (!initialized) {
            // start just called once
            super.start();
            if (queueMaxSize == -1) {
                queueMaxSize = ringBufferSize;
            }
            initialized = true;
        }
    }

    @Override
    public void run() {
        openEventHandler();
    }

    void openEventHandler() {
        try {
            int waitTimes = 60;
            for (; ; ) {
                if (shutdown || hasSubscriber() || waitTimes <= 0) {
                    break;
                }
                ThreadUtils.sleep(1000L);
                waitTimes--;
            }
            for (; ; ) {
                if (shutdown) {
                    break;
                }
                final Event event = queue.take();
                receiveEvent(event);
                updater.compareAndSet(this, lastEventSequence, Math.max(lastEventSequence, event.sequence()));
            }
        } catch (Throwable ex) {
            log.error("Event listener exception : {}", ex);
        }
    }
    private boolean hasSubscriber() {
        return (subscribers==null||subscribers.isEmpty());
    }
    @Override
    public long currentEventSize() {
        return queue.size();
    }

    @Override
    public void addSubscriber(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void removeSubscriber(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    @Override
    public boolean publish(Event event) {
        if (!initialized) {
            throw new IllegalStateException("Publisher does not start");
        }
        boolean success = this.queue.offer(event);
        if (!success) {
            log.warn("Unable to plug in due to interruption, synchronize sending time, event : {}", event);
            receiveEvent(event);
            return true;
        }
        return true;
    }
    void receiveEvent(Event event) {
        for (Subscriber subscriber : subscribers) {
            notifySubscriber(subscriber, event);
        }
    }
    @Override
    public void notifySubscriber(Subscriber subscriber, Event event) {
        final Runnable job = () -> subscriber.onEvent(event);
        final Executor executor = subscriber.executor();
        if (executor != null) {
            executor.execute(job);
        } else {
            try {
                job.run();
            } catch (Throwable e) {
                log.error("Event callback exception : {}", e);
            }
        }
    }

    @Override
    public void shutdown() throws RuntimeException {
        this.shutdown = true;
        this.queue.clear();
    }
}
