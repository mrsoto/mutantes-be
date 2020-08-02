package me.mrs.mutantes.services;

import me.mrs.mutantes.EvaluationModel;
import me.mrs.mutantes.EvaluationsRepository;
import me.mrs.mutantes.entity.EvaluationModelEntity;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@DisplayName("GIVEN an Evaluator Service")
@ExtendWith({MockitoExtension.class})
class EvaluationsServiceImplTest {

    private final Instant now = Instant.now();
    private BlockingQueue<EvaluationModel> queue;
    private EvaluationModel evaluation = new EvaluationModelEntity(List.of("AAAA", "GGGG"),
            true,
            now);
    private List<EvaluationModel> evaluationModelList = List.of(evaluation);
    private EvaluationsServiceImpl target;

    @Mock
    private EvaluationsRepository repository;

    @BeforeEach
    void init() {
        Executor executor = (task) -> {
        };
        queue = new ArrayBlockingQueue<>(10);
        target = new EvaluationsServiceImpl(1, repository, executor, queue);

    }

    @Nested
    @DisplayName("WHEN an evaluation was submitted")
    class Submitted {

        @Test
        @DisplayName("THEN should register an element in the queue")
        void registerEvaluation() {
            target.registerEvaluation(evaluation);
            BlockingQueue<EvaluationModel> queue = EvaluationsServiceImplTest.this.queue;

            Assertions.assertNotNull(queue);
            assertEquals(1, queue.size());
        }
    }

    @Nested
    @DisplayName("WHEN the there are submissions to save")
    class FLush {

        @Captor
        ArgumentCaptor<List<EvaluationModel>> listArgumentCaptor;

        @Nested
        @DisplayName("AND no errors occurs")
        class WhenFlush {

            @DisplayName("THEN insert submissions")
            @Test
            void thenInsertSubmissions() {
                var queue = EvaluationsServiceImplTest.this.queue;
                queue.addAll(evaluationModelList);
                var targetSpy = Mockito.spy(target);
                when(targetSpy.isRunning()).thenReturn(true, false);

                targetSpy.flushToRepo();

                assertEquals(0, queue.size());
                verify(repository).batchInsert(listArgumentCaptor.capture());
                var savedList = listArgumentCaptor.getValue();
                assertEquals(1, savedList.size());
                Assertions.assertEquals(evaluation, savedList.get(0));
            }
        }

        @Nested
        @DisplayName("AND there were errors saving stats")
        class WhenAreErrors {
            @DisplayName("THEN should submit to retry")
            @Test
            void whenSubmitFail() {
                doThrow(ThroedException.class).when(repository).batchInsert(any());

                var queue = EvaluationsServiceImplTest.this.queue;
                queue.addAll(evaluationModelList);
                var targetSpy = Mockito.spy(target);
                when(targetSpy.isRunning()).thenReturn(true, false);

                targetSpy.flushToRepo();

                assertEquals(1, queue.size());
                assertTrue(queue.contains(evaluation));
            }

            class ThroedException extends RuntimeException {
                public ThroedException() {
                    super("DATA ERROR");
                }
            }

        }

    }

}