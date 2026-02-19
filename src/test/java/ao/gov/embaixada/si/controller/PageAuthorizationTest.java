package ao.gov.embaixada.si.controller;

import ao.gov.embaixada.si.dto.*;
import ao.gov.embaixada.si.enums.EstadoConteudo;
import ao.gov.embaixada.si.enums.Idioma;
import ao.gov.embaixada.si.enums.TipoPagina;
import ao.gov.embaixada.si.service.PageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PageController.class)
class PageAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PageService pageService;

    @TestConfiguration
    @EnableMethodSecurity
    static class TestConfig {
        @Bean
        public PageService pageService() {
            return mock(PageService.class);
        }
    }

    private PageResponse sampleResponse() {
        return new PageResponse(UUID.randomUUID(), "test", TipoPagina.INSTITUTIONAL,
                EstadoConteudo.DRAFT, null, 1, null, null, null,
                List.of(), Instant.now(), Instant.now());
    }

    private PageCreateRequest sampleRequest() {
        return new PageCreateRequest("test-slug", TipoPagina.INSTITUTIONAL, null, 1, null, null,
                List.of(new TranslationRequest(Idioma.PT, "Test", null, null, null, null, null, null)));
    }

    @Test
    void adminCanCreatePage() throws Exception {
        when(pageService.create(any())).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/v1/pages")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isCreated());
    }

    @Test
    void editorCanCreatePage() throws Exception {
        when(pageService.create(any())).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/v1/pages")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EDITOR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isCreated());
    }

    @Test
    void viewerCanListPages() throws Exception {
        when(pageService.findAll(any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(sampleResponse())));

        mockMvc.perform(get("/api/v1/pages")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_VIEWER"))))
                .andExpect(status().isOk());
    }

    @Test
    void viewerCannotCreatePage() throws Exception {
        mockMvc.perform(post("/api/v1/pages")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_VIEWER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanDeletePage() throws Exception {
        mockMvc.perform(delete("/api/v1/pages/{id}", UUID.randomUUID())
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNoContent());
    }

    @Test
    void editorCannotDeletePage() throws Exception {
        mockMvc.perform(delete("/api/v1/pages/{id}", UUID.randomUUID())
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EDITOR"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedGets401() throws Exception {
        mockMvc.perform(get("/api/v1/pages"))
                .andExpect(status().isUnauthorized());
    }
}
