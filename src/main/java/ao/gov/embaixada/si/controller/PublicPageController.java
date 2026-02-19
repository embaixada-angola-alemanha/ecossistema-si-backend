package ao.gov.embaixada.si.controller;

import ao.gov.embaixada.commons.dto.ApiResponse;
import ao.gov.embaixada.si.dto.MenuResponse;
import ao.gov.embaixada.si.dto.PageResponse;
import ao.gov.embaixada.si.enums.LocalizacaoMenu;
import ao.gov.embaixada.si.enums.TipoPagina;
import ao.gov.embaixada.si.service.MenuService;
import ao.gov.embaixada.si.service.PageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public")
@Tag(name = "Public", description = "Public-facing CMS API (no authentication required)")
public class PublicPageController {

    private final PageService pageService;
    private final MenuService menuService;

    public PublicPageController(PageService pageService, MenuService menuService) {
        this.pageService = pageService;
        this.menuService = menuService;
    }

    @GetMapping("/pages")
    @Operation(summary = "List published pages")
    public ResponseEntity<ApiResponse<List<PageResponse>>> findPublished(
            @RequestParam(required = false) TipoPagina tipo) {
        return ResponseEntity.ok(ApiResponse.success(pageService.findPublished(tipo)));
    }

    @GetMapping("/pages/{slug}")
    @Operation(summary = "Get published page by slug")
    public ResponseEntity<ApiResponse<PageResponse>> findBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(pageService.findBySlug(slug)));
    }

    @GetMapping("/menus/{localizacao}")
    @Operation(summary = "Get active menu by location")
    public ResponseEntity<ApiResponse<MenuResponse>> findMenu(@PathVariable LocalizacaoMenu localizacao) {
        return ResponseEntity.ok(ApiResponse.success(menuService.findByLocalizacao(localizacao)));
    }
}
