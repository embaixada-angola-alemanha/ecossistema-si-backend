package ao.gov.embaixada.si.controller;

import ao.gov.embaixada.si.dto.*;
import ao.gov.embaixada.si.enums.LocalizacaoMenu;
import ao.gov.embaixada.si.service.MenuService;
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

@WebMvcTest(MenuController.class)
@AutoConfigureMockMvc(addFilters = false)
class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MenuService menuService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public MenuService menuService() {
            return mock(MenuService.class);
        }
    }

    private MenuResponse sampleMenu() {
        return new MenuResponse(UUID.randomUUID(), "Main Nav", LocalizacaoMenu.HEADER,
                true, List.of(), Instant.now(), Instant.now());
    }

    private MenuItemResponse sampleItem() {
        return new MenuItemResponse(UUID.randomUUID(), "Home", "Home", "Startseite", null,
                "/", null, null, 1, false, "home", true);
    }

    @Test
    void shouldCreateMenu() throws Exception {
        when(menuService.create(any())).thenReturn(sampleMenu());

        MenuCreateRequest request = new MenuCreateRequest("Main Nav", LocalizacaoMenu.HEADER);

        mockMvc.perform(post("/api/v1/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nome").value("Main Nav"))
                .andExpect(jsonPath("$.data.localizacao").value("HEADER"));
    }

    @Test
    void shouldGetMenuById() throws Exception {
        MenuResponse response = sampleMenu();
        when(menuService.findById(response.id())).thenReturn(response);

        mockMvc.perform(get("/api/v1/menus/{id}", response.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nome").value("Main Nav"));
    }

    @Test
    void shouldListMenus() throws Exception {
        when(menuService.findAll()).thenReturn(List.of(sampleMenu()));

        mockMvc.perform(get("/api/v1/menus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].nome").value("Main Nav"));
    }

    @Test
    void shouldAddMenuItem() throws Exception {
        UUID menuId = UUID.randomUUID();
        when(menuService.addItem(eq(menuId), any())).thenReturn(sampleItem());

        MenuItemCreateRequest request = new MenuItemCreateRequest(
                "Home", "Home", "Startseite", null, "/", null, null, 1, false, "home");

        mockMvc.perform(post("/api/v1/menus/{id}/items", menuId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.labelPt").value("Home"));
    }

    @Test
    void shouldRemoveMenuItem() throws Exception {
        UUID menuId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        doNothing().when(menuService).removeItem(menuId, itemId);

        mockMvc.perform(delete("/api/v1/menus/{menuId}/items/{itemId}", menuId, itemId))
                .andExpect(status().isNoContent());

        verify(menuService).removeItem(menuId, itemId);
    }

    @Test
    void shouldToggleMenuActive() throws Exception {
        UUID id = UUID.randomUUID();
        MenuResponse toggled = new MenuResponse(id, "Main Nav", LocalizacaoMenu.HEADER,
                false, List.of(), Instant.now(), Instant.now());
        when(menuService.toggleActive(id)).thenReturn(toggled);

        mockMvc.perform(patch("/api/v1/menus/{id}/toggle", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.activo").value(false));
    }

    @Test
    void shouldDeleteMenu() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(menuService).delete(id);

        mockMvc.perform(delete("/api/v1/menus/{id}", id))
                .andExpect(status().isNoContent());

        verify(menuService).delete(id);
    }

    @Test
    void shouldRejectCreateWithBlankName() throws Exception {
        MenuCreateRequest request = new MenuCreateRequest("", LocalizacaoMenu.HEADER);

        mockMvc.perform(post("/api/v1/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCreateWithNullLocalizacao() throws Exception {
        String json = "{\"nome\":\"Test\",\"localizacao\":null}";

        mockMvc.perform(post("/api/v1/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}
