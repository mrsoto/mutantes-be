package me.mrs.mutantes.servicios.component;

import org.springframework.stereotype.Component;

@Component
public class BaseThreading implements Threading {

    @Override
    public boolean isInterrupted() {
        return isInterrupted(currentThread());
    }

    @Override
    public boolean isInterrupted(Thread thread) {
        return thread.isInterrupted();
    }

    @Override
    public void interrupt() {
        interrupt(currentThread());
    }

    @Override
    public void interrupt(Thread thread) {
        thread.interrupt();
    }
}
