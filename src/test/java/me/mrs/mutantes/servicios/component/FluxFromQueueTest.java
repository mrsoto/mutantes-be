package me.mrs.mutantes.servicios.component;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;

import static org.mockito.Mockito.doReturn;

@ExtendWith({MockitoExtension.class})
class FluxFromQueueTest {

    @Mock
    private Threading threading;

    @Test
    void create_return_elements_until_interrupted() {
        Executor executor = Runnable::run;
        doReturn(false).doReturn(false).doReturn(true).when(threading).isInterrupted();
        var factory = new FluxFromQueue<>(threading);
        final var queue = new ArrayBlockingQueue<>(10);
        var creator = factory.create(executor, queue);
        queue.addAll(List.of("SAMPLE1", "SAMPLE2", "SAMPLE3"));

        StepVerifier
                .create(Flux.create(creator))
                .expectNext("SAMPLE1")
                .expectNext("SAMPLE2")
                .expectComplete()
                .verify();
    }

    @Test
    void when_thread_interrupted_create_return_empty() throws InterruptedException {
        Executor executor = Runnable::run;
        doReturn(true).when(threading).isInterrupted();
        var factory = new FluxFromQueue<>(threading);
        final var queue = new ArrayBlockingQueue<>(10);
        var creator = factory.create(executor, queue);
        queue.put("SAMPLE");

        Thread.currentThread().interrupt();
        StepVerifier.create(Flux.create(creator)).expectComplete().verify();
    }
}