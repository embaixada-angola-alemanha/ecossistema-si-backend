package ao.gov.embaixada.si.dto;

import java.time.Instant;
import java.util.UUID;

public record ContactInfoResponse(
        UUID id,
        String departamento,
        String endereco,
        String cidade,
        String codigoPostal,
        String pais,
        String telefone,
        String fax,
        String email,
        String horarioPt,
        String horarioEn,
        String horarioDe,
        Double latitude,
        Double longitude,
        Integer sortOrder,
        boolean activo,
        Instant createdAt,
        Instant updatedAt
) {}
