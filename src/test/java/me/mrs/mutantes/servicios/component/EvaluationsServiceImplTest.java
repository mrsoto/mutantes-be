package me.mrs.mutantes.servicios.component;

import me.mrs.mutantes.servicios.EvaluationsRepository;
import me.mrs.mutantes.servicios.domain.EvaluationModel;
import me.mrs.mutantes.servicios.domain.ModelMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@DisplayName("GIVEN an Evaluator Service")
@ExtendWith({MockitoExtension.class})
class EvaluationsServiceImplTest {

    private final Instant now = Instant.now();
    @Mock
    EvaluationsRepository repository;
    EvaluationModel evaluation = new EvaluationModel(List.of("AAAA", "GGGG"), true, now);
    List<EvaluationModel> evaluationModelList = List.of(evaluation);
    private ModelMapper modelMapper = new ModelMapper();
    private EvaluationsServiceImpl target;

    @BeforeEach
    void init() {
        Executor executor = (task) -> {
        };
        target = new EvaluationsServiceImpl(1, 1, repository, executor);

    }

    @SuppressWarnings("unchecked")
    private BlockingQueue<EvaluationModel> getInternalQueue(EvaluationsServiceImpl target) {
        return (BlockingQueue<EvaluationModel>) ReflectionTestUtils.getField(target, "queue");
    }

    @Nested
    @DisplayName("WHEN an evaluation was submitted")
    class Submitted {

        @Test
        @DisplayName("THEN should register an element in the queue")
        void registerEvaluation() {
            target.registerEvaluation(evaluation);
            BlockingQueue<EvaluationModel> queue = getInternalQueue(target);

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
                var lock = Mockito.mock(Lock.class);
                var queueAvailable = Mockito.mock(Condition.class);
                ReflectionTestUtils.setField(target, "lock", lock);
                ReflectionTestUtils.setField(target, "queueAvailable", queueAvailable);
                var queue = getInternalQueue(target);
                queue.addAll(evaluationModelList);
                var targetSpy = spy(target);
                when(targetSpy.isRunning()).thenReturn(true, false);

                ReflectionTestUtils.invokeMethod(targetSpy, "flushToRepo");

                assertEquals(0, queue.size());
                verify(repository).batchInsert(listArgumentCaptor.capture());
                var savedList = listArgumentCaptor.getValue();
                assertEquals(1, savedList.size());
                assertEquals(evaluation, savedList.get(0));
                verifyNoInteractions(lock);
            }
        }

        @Nested
        @DisplayName("AND there were errors saving stats")
        class WhenAreErrors {
            @DisplayName("THEN should submit to retry")
            @Test
            void whenSubmitFail() {
                doThrow(ThroedException.class).when(repository).batchInsert(any());

                var lock = Mockito.mock(Lock.class);
                var queueAvailable = Mockito.mock(Condition.class);
                ReflectionTestUtils.setField(target, "lock", lock);
                ReflectionTestUtils.setField(target, "queueAvailable", queueAvailable);
                var queue = getInternalQueue(target);
                queue.addAll(evaluationModelList);
                var targetSpy = spy(target);
                when(targetSpy.isRunning()).thenReturn(true, false);

                ReflectionTestUtils.invokeMethod(targetSpy, "flushToRepo");

                assertEquals(1, queue.size());
                assertTrue(queue.contains(evaluation));
                verify(lock, times(1)).lock();
                verify(lock, times(1)).unlock();
                verify(queueAvailable).signal();
            }

            class ThroedException extends DataAccessException {
                public ThroedException() {
                    super("DATA ERROR");
                }
            }

        }

    }

}