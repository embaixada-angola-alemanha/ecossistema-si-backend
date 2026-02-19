package ao.gov.embaixada.si.dto;

import ao.gov.embaixada.si.enums.Idioma;

import java.util.UUID;

public record TranslationResponse(
        UUID id,
        Idioma idioma,
        String titulo,
        String conteudo,
        String excerto,
        String metaTitulo,
        String metaDescricao,
        String metaKeywords,
        String ogImageUrl
) {}
