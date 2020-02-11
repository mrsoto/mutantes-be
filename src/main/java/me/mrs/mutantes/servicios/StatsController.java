package me.mrs.mutantes.servicios;

import me.mrs.mutantes.servicios.domain.ModelMapper;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatsController {
    private final StatsService evaluationsService;
    private final ModelMapper modelMapper;

    public StatsController(
            @NonNull StatsService evaluationsService, @NonNull ModelMapper modelMapper) {
        this.evaluationsService = evaluationsService;
        this.modelMapper = modelMapper;
    }

    @GetMapping(value = "/stats")
    public StatsViewModel getSTats() {
        return modelMapper.toViewModel(evaluationsService.getStats());
    }
}
