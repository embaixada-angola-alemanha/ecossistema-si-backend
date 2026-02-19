package ao.gov.embaixada.si.dto;

import ao.gov.embaixada.si.enums.EstadoConteudo;
import ao.gov.embaixada.si.enums.TipoPagina;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PageResponse(
        UUID id,
        String slug,
        TipoPagina tipo,
        EstadoConteudo estado,
        String template,
        Integer sortOrder,
        UUID parentId,
        UUID featuredImageId,
        Instant publishedAt,
        List<TranslationResponse> translations,
        Instant createdAt,
        Instant updatedAt
) {}
