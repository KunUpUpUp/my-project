package com.seasugar.registry.event.spi.listener;

import com.seasugar.registry.event.model.Event;

public interface Listener<E extends Event> {
    void onReceive(E event);
}
