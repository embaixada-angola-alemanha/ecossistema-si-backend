package ao.gov.embaixada.si.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record MenuItemCreateRequest(
        @NotBlank String labelPt,
        String labelEn,
        String labelDe,
        String labelCs,
        String url,
        UUID pageId,
        UUID parentId,
        Integer sortOrder,
        boolean openNewTab,
        String icon
) {}
