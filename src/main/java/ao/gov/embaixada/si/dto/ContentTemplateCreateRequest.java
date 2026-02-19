package ao.gov.embaixada.si.dto;

import ao.gov.embaixada.si.enums.TipoPagina;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ContentTemplateCreateRequest(
        @NotBlank String nome,
        String descricao,
        @NotNull TipoPagina tipoPagina,
        String templateHtml,
        String schemaJson
) {}
