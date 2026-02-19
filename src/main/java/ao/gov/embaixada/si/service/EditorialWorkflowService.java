package ao.gov.embaixada.si.service;

import ao.gov.embaixada.si.dto.PageResponse;
import ao.gov.embaixada.si.enums.EstadoConteudo;
import ao.gov.embaixada.si.exception.InvalidStateTransitionException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class EditorialWorkflowService {

    private static final Map<EstadoConteudo, Set<EstadoConteudo>> TRANSITIONS = Map.of(
            EstadoConteudo.DRAFT, Set.of(EstadoConteudo.REVIEW, EstadoConteudo.ARCHIVED),
            EstadoConteudo.REVIEW, Set.of(EstadoConteudo.DRAFT, EstadoConteudo.PUBLISHED, EstadoConteudo.ARCHIVED),
            EstadoConteudo.PUBLISHED, Set.of(EstadoConteudo.DRAFT, EstadoConteudo.ARCHIVED),
            EstadoConteudo.ARCHIVED, Set.of(EstadoConteudo.DRAFT)
    );

    private final PageService pageService;

    public EditorialWorkflowService(PageService pageService) {
        this.pageService = pageService;
    }

    public PageResponse submitForReview(UUID pageId) {
        return transition(pageId, EstadoConteudo.REVIEW);
    }

    public PageResponse publish(UUID pageId) {
        return transition(pageId, EstadoConteudo.PUBLISHED);
    }

    public PageResponse unpublish(UUID pageId) {
        return transition(pageId, EstadoConteudo.DRAFT);
    }

    public PageResponse archive(UUID pageId) {
        return transition(pageId, EstadoConteudo.ARCHIVED);
    }

    public PageResponse restore(UUID pageId) {
        return transition(pageId, EstadoConteudo.DRAFT);
    }

    private PageResponse transition(UUID pageId, EstadoConteudo novoEstado) {
        PageResponse current = pageService.findById(pageId);
        EstadoConteudo estadoActual = current.estado();

        Set<EstadoConteudo> allowed = TRANSITIONS.getOrDefault(estadoActual, Set.of());
        if (!allowed.contains(novoEstado)) {
            throw new InvalidStateTransitionException(
                    "Cannot transition from " + estadoActual + " to " + novoEstado +
                    ". Allowed: " + allowed);
        }

        return pageService.updateEstado(pageId, novoEstado);
    }
}
