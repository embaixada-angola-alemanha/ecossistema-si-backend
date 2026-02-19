package ao.gov.embaixada.si.controller;

import ao.gov.embaixada.si.dto.*;
import ao.gov.embaixada.si.enums.EstadoConteudo;
import ao.gov.embaixada.si.enums.Idioma;
import ao.gov.embaixada.si.enums.TipoPagina;
import ao.gov.embaixada.si.service.PageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PageController.class)
@AutoConfigureMockMvc(addFilters = false)
class PageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PageService pageService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public PageService pageService() {
            return mock(PageService.class);
        }
    }

    private PageResponse samplePage() {
        UUID id = UUID.randomUUID();
        return new PageResponse(id, "about-us", TipoPagina.INSTITUTIONAL,
                EstadoConteudo.DRAFT, null, 1, null, null, null,
                List.of(new TranslationResponse(UUID.randomUUID(), Idioma.PT,
                        "Sobre Nos", "<p>Conteudo</p>", "Excerto", null, null, null, null)),
                Instant.now(), Instant.now());
    }

    @Test
    void shouldCreatePage() throws Exception {
        PageResponse response = samplePage();
        when(pageService.create(any())).thenReturn(response);

        PageCreateRequest request = new PageCreateRequest(
                "about-us", TipoPagina.INSTITUTIONAL, null, 1, null, null,
                List.of(new TranslationRequest(Idioma.PT, "Sobre Nos", "<p>Conteudo</p>",
                        "Excerto", null, null, null, null)));

        mockMvc.perform(post("/api/v1/pages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.slug").value("about-us"))
                .andExpect(jsonPath("$.data.tipo").value("INSTITUTIONAL"));
    }

    @Test
    void shouldGetPageById() throws Exception {
        PageResponse response = samplePage();
        UUID id = response.id();
        when(pageService.findById(id)).thenReturn(response);

        mockMvc.perform(get("/api/v1/pages/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.slug").value("about-us"));
    }

    @Test
    void shouldGetPageBySlug() throws Exception {
        PageResponse response = samplePage();
        when(pageService.findBySlug("about-us")).thenReturn(response);

        mockMvc.perform(get("/api/v1/pages/slug/{slug}", "about-us"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.slug").value("about-us"));
    }

    @Test
    void shouldListPages() throws Exception {
        Page<PageResponse> page = new PageImpl<>(List.of(samplePage()));
        when(pageService.findAll(any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/pages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].slug").value("about-us"));
    }

    @Test
    void shouldListPagesWithFilters() throws Exception {
        Page<PageResponse> page = new PageImpl<>(List.of(samplePage()));
        when(pageService.findAll(eq(TipoPagina.INSTITUTIONAL), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/pages")
                        .param("tipo", "INSTITUTIONAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].tipo").value("INSTITUTIONAL"));
    }

    @Test
    void shouldUpdatePage() throws Exception {
        UUID id = UUID.randomUUID();
        PageResponse response = new PageResponse(id, "about-us-updated", TipoPagina.INSTITUTIONAL,
                EstadoConteudo.DRAFT, null, 1, null, null, null,
                List.of(), Instant.now(), Instant.now());
        when(pageService.update(eq(id), any())).thenReturn(response);

        PageUpdateRequest request = new PageUpdateRequest(
                "about-us-updated", null, null, null, null, null, null);

        mockMvc.perform(put("/api/v1/pages/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.slug").value("about-us-updated"));
    }

    @Test
    void shouldUpdatePageEstado() throws Exception {
        UUID id = UUID.randomUUID();
        PageResponse response = new PageResponse(id, "about-us", TipoPagina.INSTITUTIONAL,
                EstadoConteudo.PUBLISHED, null, 1, null, null, Instant.now(),
                List.of(), Instant.now(), Instant.now());
        when(pageService.updateEstado(eq(id), eq(EstadoConteudo.PUBLISHED))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/pages/{id}/estado", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"PUBLISHED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.estado").value("PUBLISHED"));
    }

    @Test
    void shouldDeletePage() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(pageService).delete(id);

        mockMvc.perform(delete("/api/v1/pages/{id}", id))
                .andExpect(status().isNoContent());

        verify(pageService).delete(id);
    }

    @Test
    void shouldSearchPages() throws Exception {
        when(pageService.search(eq("angola"), eq(Idioma.PT))).thenReturn(List.of(samplePage()));

        mockMvc.perform(get("/api/v1/pages/search")
                        .param("q", "angola")
                        .param("idioma", "PT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].slug").value("about-us"));
    }

    @Test
    void shouldRejectCreateWithBlankSlug() throws Exception {
        PageCreateRequest request = new PageCreateRequest(
                "", TipoPagina.INSTITUTIONAL, null, null, null, null,
                List.of(new TranslationRequest(Idioma.PT, "Title", null, null, null, null, null, null)));

        mockMvc.perform(post("/api/v1/pages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCreateWithEmptyTranslations() throws Exception {
        PageCreateRequest request = new PageCreateRequest(
                "test-slug", TipoPagina.INSTITUTIONAL, null, null, null, null,
                List.of());

        mockMvc.perform(post("/api/v1/pages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
