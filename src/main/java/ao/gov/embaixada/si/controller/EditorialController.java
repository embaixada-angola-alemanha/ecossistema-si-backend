package ao.gov.embaixada.si.controller;

import ao.gov.embaixada.commons.dto.ApiResponse;
import ao.gov.embaixada.si.dto.*;
import ao.gov.embaixada.si.enums.Idioma;
import ao.gov.embaixada.si.enums.TipoPagina;
import ao.gov.embaixada.si.service.ContentTemplateService;
import ao.gov.embaixada.si.service.EditorialWorkflowService;
import ao.gov.embaixada.si.service.PageVersionService;
import ao.gov.embaixada.si.service.ScheduledPublicationService;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/editorial")
@Tag(name = "Editorial", description = "Content management: workflow, versioning, scheduling, templates")
public class EditorialController {

    private final EditorialWorkflowService workflowService;
    private final PageVersionService versionService;
    private final ContentTemplateService templateService;
    private final ScheduledPublicationService scheduledService;

    public EditorialController(EditorialWorkflowService workflowService,
                                PageVersionService versionService,
                                ContentTemplateService templateService,
                                ScheduledPublicationService scheduledService) {
        this.workflowService = workflowService;
        this.versionService = versionService;
        this.templateService = templateService;
        this.scheduledService = scheduledService;
    }

    // --- Editorial Workflow ---

    @PostMapping("/pages/{id}/submit-review")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Submit page for review")
    public ResponseEntity<ApiResponse<PageResponse>> submitForReview(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success("Submitted for review",
                workflowService.submitForReview(id)));
    }

    @PostMapping("/pages/{id}/publish")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Publish page")
    public ResponseEntity<ApiResponse<PageResponse>> publish(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success("Published", workflowService.publish(id)));
    }

    @PostMapping("/pages/{id}/unpublish")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Unpublish page (back to draft)")
    public ResponseEntity<ApiResponse<PageResponse>> unpublish(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success("Unpublished", workflowService.unpublish(id)));
    }

    @PostMapping("/pages/{id}/archive")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Archive page")
    public ResponseEntity<ApiResponse<PageResponse>> archive(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success("Archived", workflowService.archive(id)));
    }

    // --- Versioning ---

    @PostMapping("/pages/{id}/versions")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Create version snapshot")
    public ResponseEntity<Void> createSnapshot(
            @PathVariable UUID id,
            @RequestParam Idioma idioma,
            @RequestParam(defaultValue = "") String summary) {
        versionService.createSnapshot(id, idioma, summary);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/pages/{id}/versions")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR','VIEWER')")
    @Operation(summary = "List page versions")
    public ResponseEntity<ApiResponse<List<PageVersionResponse>>> findVersions(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "PT") Idioma idioma,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(versionService.findVersions(id, idioma, pageable)));
    }

    @PostMapping("/pages/{id}/versions/{versionNumber}/restore")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Restore page to a specific version")
    public ResponseEntity<ApiResponse<Void>> restoreVersion(
            @PathVariable UUID id,
            @PathVariable Integer versionNumber,
            @RequestParam(defaultValue = "PT") Idioma idioma) {
        versionService.restoreVersion(id, idioma, versionNumber);
        return ResponseEntity.ok(ApiResponse.success("Restored to version " + versionNumber, null));
    }

    // --- Scheduling ---

    @PostMapping("/pages/{id}/schedule")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Schedule page publication")
    public ResponseEntity<ApiResponse<Void>> schedulePublication(
            @PathVariable UUID id, @Valid @RequestBody SchedulePublicationRequest request) {
        scheduledService.schedule(id, request.scheduledAt());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Publication scheduled", null));
    }

    // --- Content Templates ---

    @PostMapping("/templates")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create content template")
    public ResponseEntity<ApiResponse<ContentTemplateResponse>> createTemplate(
            @Valid @RequestBody ContentTemplateCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Template created", templateService.create(request)));
    }

    @GetMapping("/templates")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "List content templates")
    public ResponseEntity<ApiResponse<List<ContentTemplateResponse>>> findTemplates(
            @RequestParam(required = false) TipoPagina tipo) {
        return ResponseEntity.ok(ApiResponse.success(templateService.findAll(tipo)));
    }

    @GetMapping("/templates/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Get content template")
    public ResponseEntity<ApiResponse<ContentTemplateResponse>> findTemplate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(templateService.findById(id)));
    }

    @PutMapping("/templates/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update content template")
    public ResponseEntity<ApiResponse<ContentTemplateResponse>> updateTemplate(
            @PathVariable UUID id, @Valid @RequestBody ContentTemplateCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Template updated", templateService.update(id, request)));
    }

    @DeleteMapping("/templates/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete content template (soft)")
    public ResponseEntity<Void> deleteTemplate(@PathVariable UUID id) {
        templateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
