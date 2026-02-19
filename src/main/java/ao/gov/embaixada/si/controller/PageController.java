package ao.gov.embaixada.si.controller;

import ao.gov.embaixada.commons.dto.ApiResponse;
import ao.gov.embaixada.commons.dto.PagedResponse;
import ao.gov.embaixada.si.dto.*;
import ao.gov.embaixada.si.enums.EstadoConteudo;
import ao.gov.embaixada.si.enums.Idioma;
import ao.gov.embaixada.si.enums.TipoPagina;
import ao.gov.embaixada.si.service.PageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pages")
@Tag(name = "Pages", description = "CMS Page management")
public class PageController {

    private final PageService pageService;

    public PageController(PageService pageService) {
        this.pageService = pageService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Create page", description = "Creates a new CMS page with multilingual translations")
    public ResponseEntity<ApiResponse<PageResponse>> create(@Valid @RequestBody PageCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Page created", pageService.create(request)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR','VIEWER')")
    @Operation(summary = "Get page by ID")
    public ResponseEntity<ApiResponse<PageResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(pageService.findById(id)));
    }

    @GetMapping("/slug/{slug}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR','VIEWER')")
    @Operation(summary = "Get page by slug")
    public ResponseEntity<ApiResponse<PageResponse>> findBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(pageService.findBySlug(slug)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR','VIEWER')")
    @Operation(summary = "List pages", description = "List pages with optional filters by type and status")
    public ResponseEntity<ApiResponse<PagedResponse<PageResponse>>> findAll(
            @RequestParam(required = false) TipoPagina tipo,
            @RequestParam(required = false) EstadoConteudo estado,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                PagedResponse.of(pageService.findAll(tipo, estado, pageable))));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Update page")
    public ResponseEntity<ApiResponse<PageResponse>> update(
            @PathVariable UUID id, @Valid @RequestBody PageUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Page updated", pageService.update(id, request)));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Update page status")
    public ResponseEntity<ApiResponse<PageResponse>> updateEstado(
            @PathVariable UUID id, @RequestBody Map<String, String> body) {
        EstadoConteudo estado = EstadoConteudo.valueOf(body.get("estado"));
        return ResponseEntity.ok(ApiResponse.success(pageService.updateEstado(id, estado)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete page")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        pageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR','VIEWER')")
    @Operation(summary = "Search pages", description = "Full-text search in title and content")
    public ResponseEntity<ApiResponse<List<PageResponse>>> search(
            @RequestParam String q, @RequestParam(defaultValue = "PT") Idioma idioma) {
        return ResponseEntity.ok(ApiResponse.success(pageService.search(q, idioma)));
    }
}
