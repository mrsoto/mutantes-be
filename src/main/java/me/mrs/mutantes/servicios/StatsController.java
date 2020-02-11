package me.mrs.mutantes.servicios;

import me.mrs.mutantes.servicios.domain.ModelMapper;
import me.mrs.mutantes.servicios.domain.StatsViewModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@RestController
public class StatsController {
    private int maxAge;
    private final StatsService evaluationsService;
    private final ModelMapper modelMapper;

    public StatsController(
            @Value("${application.stats.cache.maxAge:2}") int cacheExpiresSecs,
            @NonNull StatsService evaluationsService,
            @NonNull ModelMapper modelMapper) {
        this.maxAge = cacheExpiresSecs;
        this.evaluationsService = evaluationsService;
        this.modelMapper = modelMapper;
    }

    @GetMapping(value = "/stats")
    public ResponseEntity<StatsViewModel> getStats() {
        CacheControl cacheControl = CacheControl
                .maxAge(maxAge, TimeUnit.SECONDS)
                .cachePublic()
                .staleIfError(Duration.ofMinutes(1));
        StatsViewModel body = modelMapper.toViewModel(evaluationsService.getStats());

        return ResponseEntity.ok().cacheControl(cacheControl).body(body);
    }
}
