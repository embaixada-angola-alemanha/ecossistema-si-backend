package ao.gov.embaixada.si.controller;

import ao.gov.embaixada.si.dto.EventCreateRequest;
import ao.gov.embaixada.si.dto.EventResponse;
import ao.gov.embaixada.si.enums.EstadoConteudo;
import ao.gov.embaixada.si.service.EventService;
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

@WebMvcTest(EventController.class)
@AutoConfigureMockMvc(addFilters = false)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventService eventService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public EventService eventService() {
            return mock(EventService.class);
        }
    }

    private EventResponse sampleEvent() {
        return new EventResponse(UUID.randomUUID(),
                "Dia da Independencia", "Independence Day", "Unabhaengigkeitstag", null,
                "Celebracao", "Celebration", "Feier", null,
                "Embaixada de Angola", "Embassy of Angola",
                Instant.parse("2026-11-11T09:00:00Z"), Instant.parse("2026-11-11T18:00:00Z"),
                null, EstadoConteudo.DRAFT, "PROTOCOLO",
                Instant.now(), Instant.now());
    }

    private EventCreateRequest sampleRequest() {
        return new EventCreateRequest(
                "Dia da Independencia", "Independence Day", "Unabhaengigkeitstag", null,
                "Celebracao", "Celebration", "Feier", null,
                "Embaixada de Angola", "Embassy of Angola",
                Instant.parse("2026-11-11T09:00:00Z"), Instant.parse("2026-11-11T18:00:00Z"),
                null, "PROTOCOLO");
    }

    @Test
    void shouldCreateEvent() throws Exception {
        when(eventService.create(any())).thenReturn(sampleEvent());

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.tituloPt").value("Dia da Independencia"));
    }

    @Test
    void shouldGetEventById() throws Exception {
        EventResponse response = sampleEvent();
        when(eventService.findById(response.id())).thenReturn(response);

        mockMvc.perform(get("/api/v1/events/{id}", response.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tituloPt").value("Dia da Independencia"));
    }

    @Test
    void shouldListEvents() throws Exception {
        Page<EventResponse> page = new PageImpl<>(List.of(sampleEvent()));
        when(eventService.findAll(any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].tituloPt").value("Dia da Independencia"));
    }

    @Test
    void shouldListEventsWithEstadoFilter() throws Exception {
        Page<EventResponse> page = new PageImpl<>(List.of(sampleEvent()));
        when(eventService.findAll(eq(EstadoConteudo.DRAFT), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/events")
                        .param("estado", "DRAFT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].estado").value("DRAFT"));
    }

    @Test
    void shouldUpdateEvent() throws Exception {
        UUID id = UUID.randomUUID();
        EventResponse response = sampleEvent();
        when(eventService.update(eq(id), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/events/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tituloPt").value("Dia da Independencia"));
    }

    @Test
    void shouldUpdateEventEstado() throws Exception {
        UUID id = UUID.randomUUID();
        EventResponse response = new EventResponse(id,
                "Event", null, null, null, null, null, null, null,
                null, null, Instant.now(), null, null,
                EstadoConteudo.REVIEW, null, Instant.now(), Instant.now());
        when(eventService.updateEstado(eq(id), eq(EstadoConteudo.REVIEW))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/events/{id}/estado", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"REVIEW\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.estado").value("REVIEW"));
    }

    @Test
    void shouldDeleteEvent() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(eventService).delete(id);

        mockMvc.perform(delete("/api/v1/events/{id}", id))
                .andExpect(status().isNoContent());

        verify(eventService).delete(id);
    }

    @Test
    void shouldRejectCreateWithBlankTitle() throws Exception {
        EventCreateRequest request = new EventCreateRequest(
                "", null, null, null, null, null, null, null,
                null, null, Instant.now(), null, null, null);

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCreateWithNullDataInicio() throws Exception {
        EventCreateRequest request = new EventCreateRequest(
                "Valid Title", null, null, null, null, null, null, null,
                null, null, null, null, null, null);

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
