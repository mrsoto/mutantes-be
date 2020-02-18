package me.mrs.mutantes.servicios;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
public class DocsController {
    @GetMapping("/")
    public Mono<ServerResponse> home() {
        return ServerResponse.permanentRedirect(URI.create("/docs")).build();
    }

    @GetMapping("/docs")
    public Mono<ServerResponse> docs() {
        return ServerResponse.permanentRedirect(URI.create("/docs/index.html")).build();

    }
}
