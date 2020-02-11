package me.mrs.mutantes.servicios;

import me.mrs.mutantes.servicios.component.StatsServiceImpl;
import me.mrs.mutantes.servicios.domain.ModelMapper;
import me.mrs.mutantes.servicios.domain.StatsModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatsController.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
@DisplayName("GIVEN a Stats Controller")
class StatsControllerTest {

    @MockBean
    EvaluationsRepository repository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("WHEN stats are queried THEN should response HTTP_STATUS.OK ans proper JSON")
    public void genStatsAreRetrieved() throws Exception {
        StatsModel stats = new StatsModel(10, 4);
        doReturn(stats).when(repository).getStats();

        mockMvc
                .perform(get("/stats"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{'count_human_dna':10,'count_mutant_dna':4,'ratio':0" + ".4}"))
                .andDo(document("stats"));
    }

    @TestConfiguration
    static class Config {
        @Bean
        StatsService statsService(EvaluationsRepository repository) {
            return new StatsServiceImpl(repository);
        }

        @Bean
        ModelMapper modelMapper() {
            return new ModelMapper();
        }
    }
}