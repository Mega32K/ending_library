package com.mega.endinglib.api.event.client;

import net.minecraftforge.event.TickEvent;

public class TimeStoppedClientTickEvent extends TickEvent.ClientTickEvent {
    public TimeStoppedClientTickEvent(Phase phase) {
        super(phase);
    }
}
