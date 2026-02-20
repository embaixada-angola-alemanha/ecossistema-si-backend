package ao.gov.embaixada.si.controller;

import ao.gov.embaixada.si.dto.EventCreateRequest;
import ao.gov.embaixada.si.dto.EventResponse;
import ao.gov.embaixada.si.enums.EstadoConteudo;
import ao.gov.embaixada.si.service.EventService;
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

@WebMvcTest(EventController.class)
class EventAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventService eventService;

    @TestConfiguration
    @EnableMethodSecurity
    static class TestConfig {
        @Bean
        public EventService eventService() {
            return mock(EventService.class);
        }
    }

    private EventResponse sampleResponse() {
        return new EventResponse(UUID.randomUUID(),
                "Event", null, null, null, null, null, null, null,
                null, null, Instant.now(), null, null,
                EstadoConteudo.DRAFT, null, Instant.now(), Instant.now());
    }

    private EventCreateRequest sampleRequest() {
        return new EventCreateRequest(
                "Event Title", null, null, null, "Descricao", null, null, null,
                "Embaixada", null, Instant.now(), null, null, "PROTOCOLO");
    }

    @Test
    void editorCanCreateEvent() throws Exception {
        when(eventService.create(any())).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/v1/events")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EDITOR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isCreated());
    }

    @Test
    void viewerCanListEvents() throws Exception {
        when(eventService.findAll(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(sampleResponse())));

        mockMvc.perform(get("/api/v1/events")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_VIEWER"))))
                .andExpect(status().isOk());
    }

    @Test
    void viewerCannotCreateEvent() throws Exception {
        mockMvc.perform(post("/api/v1/events")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_VIEWER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanDeleteEvent() throws Exception {
        mockMvc.perform(delete("/api/v1/events/{id}", UUID.randomUUID())
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNoContent());
    }

    @Test
    void editorCannotDeleteEvent() throws Exception {
        mockMvc.perform(delete("/api/v1/events/{id}", UUID.randomUUID())
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EDITOR"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedGets401() throws Exception {
        mockMvc.perform(get("/api/v1/events"))
                .andExpect(status().isUnauthorized());
    }
}
