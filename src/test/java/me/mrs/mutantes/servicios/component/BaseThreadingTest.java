package me.mrs.mutantes.servicios.component;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BaseThreadingTest {

    @Test
    void currentThreadIsInterrupted() {
        var threading = new BaseThreading();
        Assertions.assertFalse(threading.isInterrupted());
        Thread.currentThread().interrupt();
        Assertions.assertTrue(threading.isInterrupted());
    }

    @Test
    void threadIsInterrupted() {
        var threading = new BaseThreading();
        final var currentThread = Thread.currentThread();
        Assertions.assertFalse(threading.isInterrupted(currentThread));
        currentThread.interrupt();
        Assertions.assertTrue(threading.isInterrupted(currentThread));
    }

    @Test
    void currentThreadinterrupt() {
        var threading = new BaseThreading();
        final var currentThread = Thread.currentThread();
        Assertions.assertFalse(currentThread.isInterrupted());
        threading.interrupt();
        Assertions.assertTrue(currentThread.isInterrupted());
    }

    @Test
    void threadInterrupt() {
        var threading = new BaseThreading();
        final var currentThread = Thread.currentThread();
        Assertions.assertFalse(currentThread.isInterrupted());
        threading.interrupt(currentThread);
        Assertions.assertTrue(currentThread.isInterrupted());
    }
}