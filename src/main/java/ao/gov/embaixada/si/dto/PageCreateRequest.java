package ao.gov.embaixada.si.dto;

import ao.gov.embaixada.si.enums.TipoPagina;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record PageCreateRequest(
        @NotBlank String slug,
        @NotNull TipoPagina tipo,
        String template,
        Integer sortOrder,
        UUID parentId,
        UUID featuredImageId,
        @NotEmpty @Valid List<TranslationRequest> translations
) {}
