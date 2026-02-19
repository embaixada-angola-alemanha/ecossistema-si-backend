package ao.gov.embaixada.si.controller;

import ao.gov.embaixada.si.dto.*;
import ao.gov.embaixada.si.enums.EstadoConteudo;
import ao.gov.embaixada.si.enums.Idioma;
import ao.gov.embaixada.si.enums.TipoPagina;
import ao.gov.embaixada.si.service.ContentTemplateService;
import ao.gov.embaixada.si.service.EditorialWorkflowService;
import ao.gov.embaixada.si.service.PageVersionService;
import ao.gov.embaixada.si.service.ScheduledPublicationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EditorialController.class)
@AutoConfigureMockMvc(addFilters = false)
class EditorialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EditorialWorkflowService workflowService;

    @Autowired
    private PageVersionService versionService;

    @Autowired
    private ContentTemplateService templateService;

    @Autowired
    private ScheduledPublicationService scheduledService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public EditorialWorkflowService workflowService() {
            return mock(EditorialWorkflowService.class);
        }

        @Bean
        public PageVersionService versionService() {
            return mock(PageVersionService.class);
        }

        @Bean
        public ContentTemplateService templateService() {
            return mock(ContentTemplateService.class);
        }

        @Bean
        public ScheduledPublicationService scheduledService() {
            return mock(ScheduledPublicationService.class);
        }
    }

    private PageResponse samplePage(EstadoConteudo estado) {
        return new PageResponse(UUID.randomUUID(), "test-page", TipoPagina.INSTITUTIONAL,
                estado, null, 1, null, null, null,
                List.of(), Instant.now(), Instant.now());
    }

    // --- Workflow tests ---

    @Test
    void shouldSubmitForReview() throws Exception {
        UUID id = UUID.randomUUID();
        when(workflowService.submitForReview(id)).thenReturn(samplePage(EstadoConteudo.REVIEW));

        mockMvc.perform(post("/api/v1/editorial/pages/{id}/submit-review", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.estado").value("REVIEW"));
    }

    @Test
    void shouldPublishPage() throws Exception {
        UUID id = UUID.randomUUID();
        when(workflowService.publish(id)).thenReturn(samplePage(EstadoConteudo.PUBLISHED));

        mockMvc.perform(post("/api/v1/editorial/pages/{id}/publish", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.estado").value("PUBLISHED"));
    }

    @Test
    void shouldUnpublishPage() throws Exception {
        UUID id = UUID.randomUUID();
        when(workflowService.unpublish(id)).thenReturn(samplePage(EstadoConteudo.DRAFT));

        mockMvc.perform(post("/api/v1/editorial/pages/{id}/unpublish", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.estado").value("DRAFT"));
    }

    @Test
    void shouldArchivePage() throws Exception {
        UUID id = UUID.randomUUID();
        when(workflowService.archive(id)).thenReturn(samplePage(EstadoConteudo.ARCHIVED));

        mockMvc.perform(post("/api/v1/editorial/pages/{id}/archive", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.estado").value("ARCHIVED"));
    }

    // --- Versioning tests ---

    @Test
    void shouldCreateVersionSnapshot() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(versionService).createSnapshot(eq(id), eq(Idioma.PT), any());

        mockMvc.perform(post("/api/v1/editorial/pages/{id}/versions", id)
                        .param("idioma", "PT")
                        .param("summary", "Initial version"))
                .andExpect(status().isCreated());

        verify(versionService).createSnapshot(eq(id), eq(Idioma.PT), eq("Initial version"));
    }

    @Test
    void shouldListVersions() throws Exception {
        UUID id = UUID.randomUUID();
        PageVersionResponse version = new PageVersionResponse(UUID.randomUUID(), id,
                Idioma.PT, 1, "Title", "Content", "Excerpt",
                null, null, "Version summary", "admin", Instant.now());
        when(versionService.findVersions(eq(id), eq(Idioma.PT), any())).thenReturn(List.of(version));

        mockMvc.perform(get("/api/v1/editorial/pages/{id}/versions", id)
                        .param("idioma", "PT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].versionNumber").value(1));
    }

    @Test
    void shouldRestoreVersion() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(versionService).restoreVersion(eq(id), eq(Idioma.PT), eq(2));

        mockMvc.perform(post("/api/v1/editorial/pages/{id}/versions/{versionNumber}/restore", id, 2)
                        .param("idioma", "PT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Restored to version 2"));
    }

    // --- Scheduling tests ---

    @Test
    void shouldSchedulePublication() throws Exception {
        UUID id = UUID.randomUUID();
        Instant scheduledAt = Instant.parse("2026-03-01T10:00:00Z");
        when(scheduledService.schedule(eq(id), eq(scheduledAt))).thenReturn(null);

        mockMvc.perform(post("/api/v1/editorial/pages/{id}/schedule", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"scheduledAt\":\"2026-03-01T10:00:00Z\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Publication scheduled"));
    }

    // --- Template tests ---

    @Test
    void shouldCreateTemplate() throws Exception {
        ContentTemplateResponse templateResp = new ContentTemplateResponse(
                UUID.randomUUID(), "Landing Template", "Template for landing pages",
                TipoPagina.LANDING, "<div>{{content}}</div>", null,
                true, Instant.now(), Instant.now());
        when(templateService.create(any())).thenReturn(templateResp);

        ContentTemplateCreateRequest request = new ContentTemplateCreateRequest(
                "Landing Template", "Template for landing pages",
                TipoPagina.LANDING, "<div>{{content}}</div>", null);

        mockMvc.perform(post("/api/v1/editorial/templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.nome").value("Landing Template"));
    }

    @Test
    void shouldListTemplates() throws Exception {
        ContentTemplateResponse templateResp = new ContentTemplateResponse(
                UUID.randomUUID(), "News Template", "For news",
                TipoPagina.NEWS, "<article></article>", null,
                true, Instant.now(), Instant.now());
        when(templateService.findAll(any())).thenReturn(List.of(templateResp));

        mockMvc.perform(get("/api/v1/editorial/templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].nome").value("News Template"));
    }

    @Test
    void shouldGetTemplateById() throws Exception {
        UUID id = UUID.randomUUID();
        ContentTemplateResponse templateResp = new ContentTemplateResponse(
                id, "FAQ Template", null, TipoPagina.FAQ, "<dl></dl>", null,
                true, Instant.now(), Instant.now());
        when(templateService.findById(id)).thenReturn(templateResp);

        mockMvc.perform(get("/api/v1/editorial/templates/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nome").value("FAQ Template"));
    }

    @Test
    void shouldUpdateTemplate() throws Exception {
        UUID id = UUID.randomUUID();
        ContentTemplateResponse templateResp = new ContentTemplateResponse(
                id, "Updated Template", "Updated desc", TipoPagina.LANDING,
                "<section></section>", null, true, Instant.now(), Instant.now());
        when(templateService.update(eq(id), any())).thenReturn(templateResp);

        ContentTemplateCreateRequest request = new ContentTemplateCreateRequest(
                "Updated Template", "Updated desc", TipoPagina.LANDING,
                "<section></section>", null);

        mockMvc.perform(put("/api/v1/editorial/templates/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nome").value("Updated Template"));
    }

    @Test
    void shouldDeleteTemplate() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(templateService).delete(id);

        mockMvc.perform(delete("/api/v1/editorial/templates/{id}", id))
                .andExpect(status().isNoContent());

        verify(templateService).delete(id);
    }
}
