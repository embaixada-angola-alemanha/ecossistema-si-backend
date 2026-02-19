package ao.gov.embaixada.si.dto;

import ao.gov.embaixada.si.enums.LocalizacaoMenu;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MenuResponse(
        UUID id,
        String nome,
        LocalizacaoMenu localizacao,
        boolean activo,
        List<MenuItemResponse> items,
        Instant createdAt,
        Instant updatedAt
) {}
