package com.messagebird.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * This class is used to control the throughput
 *
 */
public class RateLimiter {
    private Semaphore semaphore;
    private int maxPermits;
    private TimeUnit timePeriod;
    private long delay;
    private ScheduledExecutorService scheduler;

    public static RateLimiter create(int maxPermits, long delay, TimeUnit timePeriod) {
	RateLimiter limiter = new RateLimiter(maxPermits, delay, timePeriod);
	limiter.schedulePermitRelease();
	return limiter;
    }

    private RateLimiter(int maxPermits, long delay, TimeUnit timePeriod) {
	this.semaphore = new Semaphore(maxPermits, true);
	this.maxPermits = maxPermits;
	this.delay = delay;
	this.timePeriod = timePeriod;
    }

    private void schedulePermitRelease() {
	scheduler = Executors.newScheduledThreadPool(1);
	scheduler.scheduleWithFixedDelay(() -> {
	    semaphore.release(maxPermits - semaphore.availablePermits());
	}, 0, delay, timePeriod);
    }

    public void acquire() throws InterruptedException {
	semaphore.acquire();
    }

    public void stop() {
	scheduler.shutdownNow();
    }
}