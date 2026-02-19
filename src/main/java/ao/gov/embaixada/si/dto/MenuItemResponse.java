package ao.gov.embaixada.si.dto;

import java.util.UUID;

public record MenuItemResponse(
        UUID id,
        String labelPt,
        String labelEn,
        String labelDe,
        String labelCs,
        String url,
        UUID pageId,
        UUID parentId,
        Integer sortOrder,
        boolean openNewTab,
        String icon,
        boolean activo
) {}
