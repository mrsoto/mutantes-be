package me.mrs.mutantes.servicios;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class DocsController {
    @GetMapping("/")
    public RedirectView home() {
        return new RedirectView("/docs");
    }

    @GetMapping("/docs")
    public RedirectView docs() {
        return new RedirectView("/docs/index.html");
    }
}
