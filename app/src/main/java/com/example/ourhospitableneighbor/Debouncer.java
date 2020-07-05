package com.example.ourhospitableneighbor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Debouncer {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture previous;

    public void debounce(Runnable runnable, long delay, TimeUnit timeUnit) {
        if (previous != null) previous.cancel(true);
        previous = scheduler.schedule(runnable, delay, timeUnit);
    }
}
