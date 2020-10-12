package com.example.demo.events;

import com.example.demo.events.listener.Subscriber;
import com.example.demo.utils.MapUtils;
import com.example.demo.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;

/**
 * 咕泡学院，只为更好的你
 * 咕泡学院-Mic: 2082233439
 * http://www.gupaoedu.com
 **/
@Slf4j
public class NotifyCenter {

    private static final NotifyCenter INSTANCE = new NotifyCenter();
    public static int ringBufferSize = 16384;

    private static BiFunction<Class<? extends Event>, Integer, EventPublisher> publisherFactory = null;

    //发布事件管理容器
    private final Map<String, EventPublisher> publisherMap = new ConcurrentHashMap<String, EventPublisher>(16);

    private static final AtomicBoolean CLOSED = new AtomicBoolean(false);

    private static Class<? extends EventPublisher> clazz = null;

    static{
        clazz = DefaultPublisher.class;
        publisherFactory = (cls, buffer) -> {
            try {
                EventPublisher publisher = clazz.newInstance();
                publisher.init(cls, buffer);
                return publisher;
            } catch (Throwable ex) {
                log.error("Service class newInstance has error : {}", ex);
                throw new RuntimeException(ex);
            }
        };
        ThreadUtils.addShutdownHook(() -> shutdown());
    }
    public static <T> void registerSubscriber(final Subscriber consumer) {
        final Class<? extends Event> cls = consumer.subscribeType();
        addSubscriber(consumer, consumer.subscribeType());
    }

    public static boolean publishEvent(final Event event) {
        try {
            return publishEvent(event.getClass(), event);
        } catch (Throwable ex) {
            log.error("There was an exception to the message publishing : {}", ex);
            return false;
        }
    }

    private static void addSubscriber(final Subscriber consumer, Class<? extends Event> subscribeType) {
        final String topic = ClassUtils.getCanonicalName(subscribeType);
        synchronized (NotifyCenter.class) {
            MapUtils.computeIfAbsent(INSTANCE.publisherMap, topic, publisherFactory, subscribeType, ringBufferSize);
        }
        EventPublisher publisher = INSTANCE.publisherMap.get(topic);
        publisher.addSubscriber(consumer);
    }

    public static EventPublisher registerToPublisher(final Class<? extends Event> eventType, final int queueMaxSize) {
        final String topic = ClassUtils.getCanonicalName(eventType);
        synchronized (NotifyCenter.class) {
            MapUtils.computeIfAbsent(INSTANCE.publisherMap, topic, publisherFactory, eventType, queueMaxSize);
        }
        EventPublisher publisher = INSTANCE.publisherMap.get(topic);
        return publisher;
    }
    private static boolean publishEvent(final Class<? extends Event> eventType, final Event event) {
        final String topic = ClassUtils.getCanonicalName(eventType);
        if (INSTANCE.publisherMap.containsKey(topic)) {
            EventPublisher publisher = INSTANCE.publisherMap.get(topic);
            return publisher.publish(event);
        }
        log.warn("There are no [{}] publishers for this event, please register", topic);
        return false;
    }

    public static void shutdown() {
        if (!CLOSED.compareAndSet(false, true)) {
            return;
        }
        log.warn("[NotifyCenter] Start destroying Publisher");
        for (Map.Entry<String, EventPublisher> entry : INSTANCE.publisherMap.entrySet()) {
            try {
                EventPublisher eventPublisher = entry.getValue();
                eventPublisher.shutdown();
            } catch (Throwable e) {
                log.error("[EventPublisher] shutdown has error : {}", e);
            }
        }
        log.warn("[NotifyCenter] Destruction of the end");
    }
}
