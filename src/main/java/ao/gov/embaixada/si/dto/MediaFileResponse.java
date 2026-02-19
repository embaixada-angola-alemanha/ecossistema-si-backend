package ao.gov.embaixada.si.dto;

import ao.gov.embaixada.si.enums.TipoMedia;

import java.time.Instant;
import java.util.UUID;

public record MediaFileResponse(
        UUID id,
        String fileName,
        String originalName,
        String mimeType,
        TipoMedia tipo,
        Long size,
        String altPt,
        String altEn,
        String altDe,
        String altCs,
        Integer width,
        Integer height,
        Instant createdAt
) {}
