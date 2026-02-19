package ao.gov.embaixada.si.dto;

import ao.gov.embaixada.si.enums.TipoPagina;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public record PageUpdateRequest(
        String slug,
        TipoPagina tipo,
        String template,
        Integer sortOrder,
        UUID parentId,
        UUID featuredImageId,
        @Valid List<TranslationRequest> translations
) {}
