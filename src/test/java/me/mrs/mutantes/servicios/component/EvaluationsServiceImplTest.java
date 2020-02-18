package me.mrs.mutantes.servicios.component;

import me.mrs.mutantes.servicios.EvaluationsRepository;
import me.mrs.mutantes.servicios.domain.EvaluationModel;
import me.mrs.mutantes.servicios.domain.ModelMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@DisplayName("GIVEN an Evaluator Service")
@ExtendWith({MockitoExtension.class})
class EvaluationsServiceImplTest {

    public static final int RETRY_COUNT = 3;
    private final Instant now = Instant.now();
    @Mock
    EvaluationsRepository repository;
    EvaluationModel evaluation = new EvaluationModel(List.of("AAAA", "GGGG"), true, now);
    List<EvaluationModel> evaluationModelList = List.of(evaluation);
    private ModelMapper modelMapper = new ModelMapper();
    private EvaluationsServiceImpl target;
    final Executor immediateExecutor = (task) -> {
    };
    @Mock
    BlockingQueue<EvaluationModel> queueMock;
    @Captor
    private ArgumentCaptor<List<EvaluationModel>> listArgumentCaptor;
    @Mock
    private FluxFromQueue<EvaluationModel> fluxCreatorFactory;

    @BeforeEach
    void setUp() {
        final Scheduler immediateScheduler = Schedulers.immediate();

        target = new EvaluationsServiceImpl(1,
                0,
                0,
                RETRY_COUNT,
                repository,
                immediateExecutor,
                fluxCreatorFactory,
                immediateScheduler);

    }

    @SuppressWarnings("unchecked")
    private BlockingQueue<EvaluationModel> getInternalQueue(EvaluationsServiceImpl target) {
        return (BlockingQueue<EvaluationModel>) ReflectionTestUtils.getField(target, "queue");
    }

    <T> Consumer<FluxSink<T>> simpleFluxCreator(Collection<T> queue) {
        return (FluxSink<T> sink) -> {
            queue.forEach(sink::next);
            sink.complete();
        };
    }

    <T> Consumer<FluxSink<T>> simpleErrFluxCreator(T element) {
        return (FluxSink<T> sink) -> {
            sink.error(new InterruptedException());
            sink.next(element);
        };
    }

    @Nested
    @DisplayName("WHEN lice cycle")
    class WhenLifeCycle {

        @Test
        @DisplayName("AND init THEN setup teh saving flux")
        void whenInit() {
            var queue = getInternalQueue(target);
            when(fluxCreatorFactory.create(same(immediateExecutor), same(queue))).thenReturn(
                    simpleFluxCreator(evaluationModelList));

            target.init();

            verify(fluxCreatorFactory).create(same(immediateExecutor), same(queue));
        }
    }

    @Nested
    @DisplayName("WHEN an evaluation was submitted")
    class Submitted {

        @Test
        @DisplayName("THEN should register an element in the queue")
        void registerEvaluation() {
            var queue = getInternalQueue(target);

            target.registerEvaluation(evaluation);

            Assertions.assertNotNull(queue);
            assertEquals(1, queue.size());
        }
    }

    @Nested
    @DisplayName("WHEN the there are submissions to save")
    class WhenEvaluationsToSave {

        @Captor
        ArgumentCaptor<List<EvaluationModel>> listArgumentCaptor;

        @Nested
        @DisplayName("AND no errors occurs")
        class WhenFlush {

            @DisplayName("THEN should insert submissions in the queue")
            @Test
            void thenInsertSubmissions() {
                var queue = getInternalQueue(target);
                var targetSpy = spy(target);
                targetSpy.registerEvaluation(evaluation);

                assertEquals(1, queue.size());
                assertEquals(evaluation, queue.peek());
            }
        }

        @Nested
        @DisplayName("AND the saving flux was created")
        class WhenSavingFluxCreated {

            @Test
            @DisplayName("AND save success THEN should return saving count")
            @SuppressWarnings("unchecked")
            void whenSaved() {
                when(fluxCreatorFactory.create(any(), any())).thenReturn(simpleFluxCreator(
                        evaluationModelList));
                var rx = (ParallelFlux<Integer>) ReflectionTestUtils.invokeMethod(target,
                        "createSavingFlux");
                assertNotNull(rx);
                StepVerifier.create(rx).expectNext(1).expectComplete().verify();

                verify(repository, times(1)).batchInsert(listArgumentCaptor.capture());
                Assertions.assertIterableEquals(List.of(evaluation), listArgumentCaptor.getValue());

            }

            @Test
            @DisplayName("AND save fail THAN should retry")
            @SuppressWarnings("unchecked")
            void whenSaveFailThenRetried() {
                when(fluxCreatorFactory.create(any(), any())).thenReturn(simpleFluxCreator(
                        evaluationModelList));
                doThrow(ThroedException.class).when(repository).batchInsert(any());
                var input = Flux.just(evaluation);
                var rx = (ParallelFlux<Integer>) ReflectionTestUtils.invokeMethod(target,
                        "createSavingFlux");
                assertNotNull(rx);
                StepVerifier.create(rx).expectError(IllegalStateException.class).verify();

                verify(repository,
                        times(RETRY_COUNT + 1)).batchInsert(listArgumentCaptor.capture());

            }

            @Test
            @DisplayName("AND was interrupted THAN should retry")
            @SuppressWarnings("unchecked")
            void whenInterruptThenRetried() {
                when(fluxCreatorFactory.create(any(), any())).thenReturn(simpleErrFluxCreator(
                        evaluation));
                var rx = (ParallelFlux<Integer>) ReflectionTestUtils.invokeMethod(target,
                        "createSavingFlux");
                assertNotNull(rx);
                StepVerifier.create(rx).expectError(InterruptedException.class).verify();

                verify(repository, never()).batchInsert(any());
            }
        }

        class ThroedException extends DataAccessException {
            public ThroedException() {
                super("DATA ERROR");
            }
        }
    }

}