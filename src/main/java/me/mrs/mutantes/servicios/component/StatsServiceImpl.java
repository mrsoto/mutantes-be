package me.mrs.mutantes.servicios.component;

import me.mrs.mutantes.servicios.EvaluationsRepository;
import me.mrs.mutantes.servicios.StatsService;
import me.mrs.mutantes.servicios.domain.StatsModel;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class StatsServiceImpl implements StatsService {

    private final EvaluationsRepository repository;

    public StatsServiceImpl(@NonNull EvaluationsRepository repository) {
        this.repository = repository;
    }

    @Override
    @Cacheable("stats")
    public StatsModel getStats() {
        return repository.getStats();
    }
}
