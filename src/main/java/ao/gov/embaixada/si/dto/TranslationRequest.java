package ao.gov.embaixada.si.dto;

import ao.gov.embaixada.si.enums.Idioma;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TranslationRequest(
        @NotNull Idioma idioma,
        @NotBlank String titulo,
        String conteudo,
        String excerto,
        String metaTitulo,
        String metaDescricao,
        String metaKeywords,
        String ogImageUrl
) {}
