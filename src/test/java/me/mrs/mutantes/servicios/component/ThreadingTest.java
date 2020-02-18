package me.mrs.mutantes.servicios.component;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ThreadingTest {

    @Test
    void currentThread() {
        var threading = Mockito.spy(Threading.class);
        Assertions.assertSame(Thread.currentThread(), threading.currentThread());
    }
}