package ao.gov.embaixada.si.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record EventCreateRequest(
        @NotBlank String tituloPt,
        String tituloEn,
        String tituloDe,
        String tituloCs,
        String descricaoPt,
        String descricaoEn,
        String descricaoDe,
        String descricaoCs,
        String localPt,
        String localEn,
        @NotNull Instant dataInicio,
        Instant dataFim,
        UUID imageId,
        String tipoEvento
) {}
