package ao.gov.embaixada.si.controller;

import ao.gov.embaixada.commons.dto.ApiResponse;
import ao.gov.embaixada.si.dto.ContactInfoCreateRequest;
import ao.gov.embaixada.si.dto.ContactInfoResponse;
import ao.gov.embaixada.si.service.ContactInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/contacts")
@Tag(name = "Contacts", description = "Contact information management")
public class ContactController {

    private final ContactInfoService contactInfoService;

    public ContactController(ContactInfoService contactInfoService) {
        this.contactInfoService = contactInfoService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Create contact info")
    public ResponseEntity<ApiResponse<ContactInfoResponse>> create(
            @Valid @RequestBody ContactInfoCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Contact created", contactInfoService.create(request)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR','VIEWER')")
    @Operation(summary = "Get contact by ID")
    public ResponseEntity<ApiResponse<ContactInfoResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(contactInfoService.findById(id)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR','VIEWER')")
    @Operation(summary = "List all contacts")
    public ResponseEntity<ApiResponse<List<ContactInfoResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(contactInfoService.findAll()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Update contact info")
    public ResponseEntity<ApiResponse<ContactInfoResponse>> update(
            @PathVariable UUID id, @Valid @RequestBody ContactInfoCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Contact updated", contactInfoService.update(id, request)));
    }

    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Toggle contact active status")
    public ResponseEntity<ApiResponse<ContactInfoResponse>> toggleActive(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(contactInfoService.toggleActive(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete contact info")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        contactInfoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
