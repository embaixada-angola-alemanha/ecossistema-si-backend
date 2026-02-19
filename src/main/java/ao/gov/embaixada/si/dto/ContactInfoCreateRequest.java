package ao.gov.embaixada.si.dto;

import jakarta.validation.constraints.NotBlank;

public record ContactInfoCreateRequest(
        @NotBlank String departamento,
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
        Integer sortOrder
) {}
