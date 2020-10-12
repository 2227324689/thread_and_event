package com.example.demo.events.listener;

import com.example.demo.events.Event;

import java.util.concurrent.Executor;

/**
 * 咕泡学院，只为更好的你
 * 咕泡学院-Mic: 2082233439
 * http://www.gupaoedu.com
 **/
public abstract class Subscriber<T extends Event> {

    /**
     * Event callback.
     *
     * @param event {@link Event}
     */
    public abstract void onEvent(T event);

    public abstract Class<? extends Event> subscribeType();

    public Executor executor() {return null;}

    public boolean ignoreExpireEvent() {
        return false;
    }

}
