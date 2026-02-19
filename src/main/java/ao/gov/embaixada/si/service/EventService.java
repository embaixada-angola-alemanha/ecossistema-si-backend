package ao.gov.embaixada.si.service;

import ao.gov.embaixada.si.dto.EventCreateRequest;
import ao.gov.embaixada.si.dto.EventResponse;
import ao.gov.embaixada.si.entity.Event;
import ao.gov.embaixada.si.enums.EstadoConteudo;
import ao.gov.embaixada.si.exception.InvalidStateTransitionException;
import ao.gov.embaixada.si.exception.ResourceNotFoundException;
import ao.gov.embaixada.si.repository.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public EventResponse create(EventCreateRequest request) {
        Event event = new Event();
        mapRequestToEntity(request, event);
        return toResponse(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    public EventResponse findById(UUID id) {
        return toResponse(eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + id)));
    }

    @Transactional(readOnly = true)
    public Page<EventResponse> findAll(EstadoConteudo estado, Pageable pageable) {
        if (estado != null) {
            return eventRepository.findByEstadoOrderByDataInicioDesc(estado, pageable)
                    .map(this::toResponse);
        }
        return eventRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<EventResponse> findByTipo(String tipoEvento, Pageable pageable) {
        return eventRepository.findByTipoEventoAndEstadoOrderByDataInicioDesc(
                tipoEvento, EstadoConteudo.PUBLISHED, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> findUpcoming() {
        return eventRepository.findByEstadoAndDataInicioAfterOrderByDataInicioAsc(
                EstadoConteudo.PUBLISHED, Instant.now())
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<EventResponse> findByDateRange(Instant start, Instant end) {
        return eventRepository.findByEstadoAndDataInicioBetweenOrderByDataInicioAsc(
                EstadoConteudo.PUBLISHED, start, end)
                .stream().map(this::toResponse).toList();
    }

    public EventResponse update(UUID id, EventCreateRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + id));
        mapRequestToEntity(request, event);
        return toResponse(eventRepository.save(event));
    }

    public EventResponse updateEstado(UUID id, EstadoConteudo novoEstado) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + id));

        validateTransition(event.getEstado(), novoEstado);
        event.setEstado(novoEstado);
        return toResponse(eventRepository.save(event));
    }

    public void delete(UUID id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Event not found: " + id);
        }
        eventRepository.deleteById(id);
    }

    private void validateTransition(EstadoConteudo current, EstadoConteudo target) {
        boolean valid = switch (current) {
            case DRAFT -> target == EstadoConteudo.REVIEW || target == EstadoConteudo.ARCHIVED;
            case REVIEW -> target == EstadoConteudo.DRAFT || target == EstadoConteudo.PUBLISHED || target == EstadoConteudo.ARCHIVED;
            case PUBLISHED -> target == EstadoConteudo.DRAFT || target == EstadoConteudo.ARCHIVED;
            case ARCHIVED -> target == EstadoConteudo.DRAFT;
        };
        if (!valid) {
            throw new InvalidStateTransitionException(
                    "Cannot transition event from " + current + " to " + target);
        }
    }

    private void mapRequestToEntity(EventCreateRequest request, Event event) {
        event.setTituloPt(request.tituloPt());
        event.setTituloEn(request.tituloEn());
        event.setTituloDe(request.tituloDe());
        event.setTituloCs(request.tituloCs());
        event.setDescricaoPt(request.descricaoPt());
        event.setDescricaoEn(request.descricaoEn());
        event.setDescricaoDe(request.descricaoDe());
        event.setDescricaoCs(request.descricaoCs());
        event.setLocalPt(request.localPt());
        event.setLocalEn(request.localEn());
        event.setDataInicio(request.dataInicio());
        event.setDataFim(request.dataFim());
        event.setImageId(request.imageId());
        event.setTipoEvento(request.tipoEvento());
    }

    private EventResponse toResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getTituloPt(),
                event.getTituloEn(),
                event.getTituloDe(),
                event.getTituloCs(),
                event.getDescricaoPt(),
                event.getDescricaoEn(),
                event.getDescricaoDe(),
                event.getDescricaoCs(),
                event.getLocalPt(),
                event.getLocalEn(),
                event.getDataInicio(),
                event.getDataFim(),
                event.getImageId(),
                event.getEstado(),
                event.getTipoEvento(),
                event.getCreatedAt(),
                event.getUpdatedAt());
    }
}
