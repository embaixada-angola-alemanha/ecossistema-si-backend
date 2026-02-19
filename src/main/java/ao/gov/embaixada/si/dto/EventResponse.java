package ao.gov.embaixada.si.dto;

import ao.gov.embaixada.si.enums.EstadoConteudo;

import java.time.Instant;
import java.util.UUID;

public record EventResponse(
        UUID id,
        String tituloPt,
        String tituloEn,
        String tituloDe,
        String tituloCs,
        String descricaoPt,
        String descricaoEn,
        String descricaoDe,
        String descricaoCs,
        String localPt,
        String localEn,
        Instant dataInicio,
        Instant dataFim,
        UUID imageId,
        EstadoConteudo estado,
        String tipoEvento,
        Instant createdAt,
        Instant updatedAt
) {}
