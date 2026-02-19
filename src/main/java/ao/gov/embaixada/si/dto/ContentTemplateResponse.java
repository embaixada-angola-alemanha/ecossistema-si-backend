package ao.gov.embaixada.si.dto;

import ao.gov.embaixada.si.enums.TipoPagina;

import java.time.Instant;
import java.util.UUID;

public record ContentTemplateResponse(
        UUID id,
        String nome,
        String descricao,
        TipoPagina tipoPagina,
        String templateHtml,
        String schemaJson,
        boolean activo,
        Instant createdAt,
        Instant updatedAt
) {}
