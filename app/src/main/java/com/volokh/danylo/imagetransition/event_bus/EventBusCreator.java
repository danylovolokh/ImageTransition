package com.volokh.danylo.imagetransition.event_bus;

import com.squareup.otto.Bus;

/**
 * Created by danylo.volokh on 3/14/16.
 * Double checked singleton (basically we need it only in UI thread) for Otto event bus.
 */
public class EventBusCreator {

    private static Bus bus;

    public static Bus defaultEventBus() {

        if (bus == null) {
            synchronized (EventBusCreator.class) {
                if (bus == null) {
                    bus = new Bus();
                }
            }
        }
        return bus;
    }
}
