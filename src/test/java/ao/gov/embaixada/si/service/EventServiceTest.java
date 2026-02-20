package ao.gov.embaixada.si.service;

import ao.gov.embaixada.si.dto.EventCreateRequest;
import ao.gov.embaixada.si.dto.EventResponse;
import ao.gov.embaixada.si.entity.Event;
import ao.gov.embaixada.si.enums.EstadoConteudo;
import ao.gov.embaixada.si.exception.IncompleteContentException;
import ao.gov.embaixada.si.exception.InvalidStateTransitionException;
import ao.gov.embaixada.si.exception.ResourceNotFoundException;
import ao.gov.embaixada.si.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    private Event createEventEntity(String titulo, EstadoConteudo estado) {
        Event event = new Event();
        event.setId(UUID.randomUUID());
        event.setTituloPt(titulo);
        event.setTituloEn("English Title");
        event.setTituloDe("German Title");
        event.setTituloCs("Czech Title");
        event.setDescricaoPt("Descricao PT");
        event.setDescricaoEn("Description EN");
        event.setDescricaoDe("Beschreibung DE");
        event.setDescricaoCs("Popis CS");
        event.setLocalPt("Embaixada de Angola, Berlim");
        event.setLocalEn("Embassy of Angola, Berlin");
        event.setEstado(estado);
        event.setDataInicio(Instant.parse("2026-11-11T09:00:00Z"));
        event.setDataFim(Instant.parse("2026-11-11T18:00:00Z"));
        event.setTipoEvento("PROTOCOLO");
        event.setCreatedAt(Instant.now());
        event.setUpdatedAt(Instant.now());
        return event;
    }

    private Event createIncompleteEventEntity(EstadoConteudo estado) {
        Event event = new Event();
        event.setId(UUID.randomUUID());
        event.setTituloPt("Evento Incompleto");
        event.setDescricaoPt("Descricao PT");
        event.setLocalPt("Embaixada");
        event.setEstado(estado);
        event.setDataInicio(Instant.parse("2026-11-11T09:00:00Z"));
        event.setTipoEvento("PROTOCOLO");
        event.setCreatedAt(Instant.now());
        event.setUpdatedAt(Instant.now());
        return event;
    }

    private EventCreateRequest sampleRequest() {
        return new EventCreateRequest(
                "Evento Teste", "Test Event", null, null,
                "Descricao", "Description", null, null,
                "Embaixada", "Embassy",
                Instant.parse("2026-11-11T09:00:00Z"),
                Instant.parse("2026-11-11T18:00:00Z"),
                null, "PROTOCOLO");
    }

    @Test
    void shouldCreateEvent() {
        Event entity = createEventEntity("Evento Teste", EstadoConteudo.DRAFT);
        when(eventRepository.save(any(Event.class))).thenReturn(entity);

        EventResponse response = eventService.create(sampleRequest());

        assertNotNull(response);
        assertEquals("Evento Teste", response.tituloPt());
        assertEquals(EstadoConteudo.DRAFT, response.estado());
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void shouldFindById() {
        UUID id = UUID.randomUUID();
        Event entity = createEventEntity("Found", EstadoConteudo.PUBLISHED);
        entity.setId(id);
        when(eventRepository.findById(id)).thenReturn(Optional.of(entity));

        EventResponse response = eventService.findById(id);

        assertEquals("Found", response.tituloPt());
    }

    @Test
    void shouldThrowNotFoundById() {
        UUID id = UUID.randomUUID();
        when(eventRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.findById(id));
    }

    @Test
    void shouldFindAllPaged() {
        Event entity = createEventEntity("Listed", EstadoConteudo.DRAFT);
        Page<Event> page = new PageImpl<>(List.of(entity));
        when(eventRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<EventResponse> result = eventService.findAll(null, Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldFindAllByEstado() {
        Event entity = createEventEntity("Published Event", EstadoConteudo.PUBLISHED);
        Page<Event> page = new PageImpl<>(List.of(entity));
        when(eventRepository.findByEstadoOrderByDataInicioDesc(EstadoConteudo.PUBLISHED, Pageable.unpaged()))
                .thenReturn(page);

        Page<EventResponse> result = eventService.findAll(EstadoConteudo.PUBLISHED, Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldUpdateEvent() {
        UUID id = UUID.randomUUID();
        Event entity = createEventEntity("Old Title", EstadoConteudo.DRAFT);
        entity.setId(id);
        when(eventRepository.findById(id)).thenReturn(Optional.of(entity));
        when(eventRepository.save(any(Event.class))).thenReturn(entity);

        EventResponse response = eventService.update(id, sampleRequest());

        assertNotNull(response);
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void shouldThrowNotFoundOnUpdate() {
        UUID id = UUID.randomUUID();
        when(eventRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.update(id, sampleRequest()));
    }

    @Test
    void shouldUpdateEstadoDraftToReview() {
        UUID id = UUID.randomUUID();
        Event entity = createEventEntity("Event", EstadoConteudo.DRAFT);
        entity.setId(id);
        when(eventRepository.findById(id)).thenReturn(Optional.of(entity));
        when(eventRepository.save(any(Event.class))).thenReturn(entity);

        EventResponse response = eventService.updateEstado(id, EstadoConteudo.REVIEW);

        assertNotNull(response);
    }

    @Test
    void shouldUpdateEstadoReviewToPublished() {
        UUID id = UUID.randomUUID();
        Event entity = createEventEntity("Event", EstadoConteudo.REVIEW);
        entity.setId(id);
        when(eventRepository.findById(id)).thenReturn(Optional.of(entity));
        when(eventRepository.save(any(Event.class))).thenReturn(entity);

        EventResponse response = eventService.updateEstado(id, EstadoConteudo.PUBLISHED);

        assertNotNull(response);
    }

    @Test
    void shouldRejectInvalidTransitionDraftToPublished() {
        UUID id = UUID.randomUUID();
        Event entity = createEventEntity("Event", EstadoConteudo.DRAFT);
        entity.setId(id);
        when(eventRepository.findById(id)).thenReturn(Optional.of(entity));

        assertThrows(InvalidStateTransitionException.class,
                () -> eventService.updateEstado(id, EstadoConteudo.PUBLISHED));
    }

    @Test
    void shouldRejectInvalidTransitionArchivedToPublished() {
        UUID id = UUID.randomUUID();
        Event entity = createEventEntity("Event", EstadoConteudo.ARCHIVED);
        entity.setId(id);
        when(eventRepository.findById(id)).thenReturn(Optional.of(entity));

        assertThrows(InvalidStateTransitionException.class,
                () -> eventService.updateEstado(id, EstadoConteudo.PUBLISHED));
    }

    @Test
    void shouldAllowArchivedToDraft() {
        UUID id = UUID.randomUUID();
        Event entity = createEventEntity("Event", EstadoConteudo.ARCHIVED);
        entity.setId(id);
        when(eventRepository.findById(id)).thenReturn(Optional.of(entity));
        when(eventRepository.save(any(Event.class))).thenReturn(entity);

        EventResponse response = eventService.updateEstado(id, EstadoConteudo.DRAFT);

        assertNotNull(response);
    }

    @Test
    void shouldDeleteEvent() {
        UUID id = UUID.randomUUID();
        when(eventRepository.existsById(id)).thenReturn(true);
        doNothing().when(eventRepository).deleteById(id);

        eventService.delete(id);

        verify(eventRepository).deleteById(id);
    }

    @Test
    void shouldThrowNotFoundOnDelete() {
        UUID id = UUID.randomUUID();
        when(eventRepository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> eventService.delete(id));
    }

    @Test
    void shouldFindUpcoming() {
        Event entity = createEventEntity("Future Event", EstadoConteudo.PUBLISHED);
        when(eventRepository.findByEstadoAndDataInicioAfterOrderByDataInicioAsc(
                eq(EstadoConteudo.PUBLISHED), any(Instant.class)))
                .thenReturn(List.of(entity));

        List<EventResponse> result = eventService.findUpcoming();

        assertEquals(1, result.size());
    }

    @Test
    void shouldFindByDateRange() {
        Event entity = createEventEntity("Range Event", EstadoConteudo.PUBLISHED);
        Instant start = Instant.parse("2026-01-01T00:00:00Z");
        Instant end = Instant.parse("2026-12-31T23:59:59Z");
        when(eventRepository.findByEstadoAndDataInicioBetweenOrderByDataInicioAsc(
                EstadoConteudo.PUBLISHED, start, end))
                .thenReturn(List.of(entity));

        List<EventResponse> result = eventService.findByDateRange(start, end);

        assertEquals(1, result.size());
    }

    @Test
    void shouldRejectPublishWithMissingTranslations() {
        UUID id = UUID.randomUUID();
        Event entity = createIncompleteEventEntity(EstadoConteudo.REVIEW);
        entity.setId(id);
        when(eventRepository.findById(id)).thenReturn(Optional.of(entity));

        IncompleteContentException ex = assertThrows(IncompleteContentException.class,
                () -> eventService.updateEstado(id, EstadoConteudo.PUBLISHED));

        assertTrue(ex.getMessage().contains("tituloEn"));
        assertTrue(ex.getMessage().contains("descricaoEn"));
        assertTrue(ex.getMessage().contains("localEn"));
    }

    @Test
    void shouldPublishWithAllTranslationsComplete() {
        UUID id = UUID.randomUUID();
        Event entity = createEventEntity("Complete Event", EstadoConteudo.REVIEW);
        entity.setId(id);
        when(eventRepository.findById(id)).thenReturn(Optional.of(entity));
        when(eventRepository.save(any(Event.class))).thenReturn(entity);

        EventResponse response = eventService.updateEstado(id, EstadoConteudo.PUBLISHED);

        assertNotNull(response);
    }
}
