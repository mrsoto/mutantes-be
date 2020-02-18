package me.mrs.mutantes.servicios.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

@Component
public class FluxFromQueue<T> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Threading threading;

    public FluxFromQueue(Threading threading) {
        this.threading = threading;
    }

    @SuppressWarnings("squid:S2142")
    Consumer<FluxSink<T>> create(Executor executor, BlockingQueue<T> queue) {
        return (FluxSink<T> sink) -> executor.execute(() -> {
            try {
                while (!threading.isInterrupted()) {
                    T event = queue.take();
                    logger.debug("Got Event: {}", event);
                    sink.next(event);
                }
            } catch (InterruptedException e) {
                logger.info("Interrupted");
                threading.interrupt();
            } finally {
                sink.complete();
            }
        });
    }

}