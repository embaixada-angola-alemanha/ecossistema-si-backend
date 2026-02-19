package ao.gov.embaixada.si.dto;

import ao.gov.embaixada.si.enums.Idioma;

import java.time.Instant;
import java.util.UUID;

public record PageVersionResponse(
        UUID id,
        UUID pageId,
        Idioma idioma,
        Integer versionNumber,
        String titulo,
        String conteudo,
        String excerto,
        String metaTitulo,
        String metaDescricao,
        String changeSummary,
        String createdBy,
        Instant createdAt
) {}
