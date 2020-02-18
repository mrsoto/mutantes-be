package me.mrs.mutantes.servicios.component;

public interface Threading {
    default Thread currentThread() {
        return Thread.currentThread();
    }

    boolean isInterrupted();

    boolean isInterrupted(Thread thread);

    void interrupt();

    void interrupt(Thread thread);
}
