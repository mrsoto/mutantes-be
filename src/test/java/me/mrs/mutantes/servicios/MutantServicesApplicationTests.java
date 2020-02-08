package me.mrs.mutantes.servicios;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DisplayName("GIVEN an application")
class MutantServicesApplicationTests {
    @Autowired
    ApplicationContext context;

    @Test
    @DisplayName("THEN the context should start")
    void contextLoads() {
        assertNotNull(context);
    }

}
