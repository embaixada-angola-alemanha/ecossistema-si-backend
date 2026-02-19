package ao.gov.embaixada.si.controller;

import ao.gov.embaixada.si.dto.ContactInfoCreateRequest;
import ao.gov.embaixada.si.dto.ContactInfoResponse;
import ao.gov.embaixada.si.service.ContactInfoService;
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

@WebMvcTest(ContactController.class)
@AutoConfigureMockMvc(addFilters = false)
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ContactInfoService contactInfoService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ContactInfoService contactInfoService() {
            return mock(ContactInfoService.class);
        }
    }

    private ContactInfoResponse sampleContact() {
        return new ContactInfoResponse(UUID.randomUUID(),
                "Secao Consular", "Wallstrasse 58", "Berlin", "10179", "Alemanha",
                "+49 30 240 8970", "+49 30 240 8979", "info@botschaft-angola.de",
                "Seg-Sex: 09:00-16:00", "Mon-Fri: 09:00-16:00", "Mo-Fr: 09:00-16:00",
                52.5120, 13.4050, 1, true, Instant.now(), Instant.now());
    }

    private ContactInfoCreateRequest sampleRequest() {
        return new ContactInfoCreateRequest(
                "Secao Consular", "Wallstrasse 58", "Berlin", "10179", "Alemanha",
                "+49 30 240 8970", "+49 30 240 8979", "info@botschaft-angola.de",
                "Seg-Sex: 09:00-16:00", "Mon-Fri: 09:00-16:00", "Mo-Fr: 09:00-16:00",
                52.5120, 13.4050, 1);
    }

    @Test
    void shouldCreateContact() throws Exception {
        when(contactInfoService.create(any())).thenReturn(sampleContact());

        mockMvc.perform(post("/api/v1/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.departamento").value("Secao Consular"));
    }

    @Test
    void shouldGetContactById() throws Exception {
        ContactInfoResponse response = sampleContact();
        when(contactInfoService.findById(response.id())).thenReturn(response);

        mockMvc.perform(get("/api/v1/contacts/{id}", response.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.departamento").value("Secao Consular"))
                .andExpect(jsonPath("$.data.cidade").value("Berlin"));
    }

    @Test
    void shouldListContacts() throws Exception {
        when(contactInfoService.findAll()).thenReturn(List.of(sampleContact()));

        mockMvc.perform(get("/api/v1/contacts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].departamento").value("Secao Consular"));
    }

    @Test
    void shouldUpdateContact() throws Exception {
        UUID id = UUID.randomUUID();
        ContactInfoResponse updated = new ContactInfoResponse(id,
                "Secao Consular Updated", "Wallstrasse 58", "Berlin", "10179", "Alemanha",
                "+49 30 240 8970", null, "new@email.de",
                null, null, null, null, null, 1, true, Instant.now(), Instant.now());
        when(contactInfoService.update(eq(id), any())).thenReturn(updated);

        mockMvc.perform(put("/api/v1/contacts/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.departamento").value("Secao Consular Updated"));
    }

    @Test
    void shouldToggleContactActive() throws Exception {
        UUID id = UUID.randomUUID();
        ContactInfoResponse toggled = new ContactInfoResponse(id,
                "Secao Consular", null, null, null, null,
                null, null, null, null, null, null, null, null, null,
                false, Instant.now(), Instant.now());
        when(contactInfoService.toggleActive(id)).thenReturn(toggled);

        mockMvc.perform(patch("/api/v1/contacts/{id}/toggle-active", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.activo").value(false));
    }

    @Test
    void shouldDeleteContact() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(contactInfoService).delete(id);

        mockMvc.perform(delete("/api/v1/contacts/{id}", id))
                .andExpect(status().isNoContent());

        verify(contactInfoService).delete(id);
    }

    @Test
    void shouldRejectCreateWithBlankDepartamento() throws Exception {
        ContactInfoCreateRequest request = new ContactInfoCreateRequest(
                "", null, null, null, null, null, null, null,
                null, null, null, null, null, null);

        mockMvc.perform(post("/api/v1/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
