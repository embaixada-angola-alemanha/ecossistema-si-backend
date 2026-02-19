package ao.gov.embaixada.si.controller;

import ao.gov.embaixada.commons.dto.ApiResponse;
import ao.gov.embaixada.commons.dto.PagedResponse;
import ao.gov.embaixada.si.dto.MediaFileResponse;
import ao.gov.embaixada.si.enums.TipoMedia;
import ao.gov.embaixada.si.service.MediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/media")
@Tag(name = "Media", description = "Media library management")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Upload media file")
    public ResponseEntity<ApiResponse<MediaFileResponse>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String altPt,
            @RequestParam(required = false) String altEn,
            @RequestParam(required = false) String altDe,
            @RequestParam(required = false) String altCs) {
        return ResponseEntity.status(201)
                .body(ApiResponse.success("File uploaded", mediaService.upload(file, altPt, altEn, altDe, altCs)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR','VIEWER')")
    @Operation(summary = "Get media metadata")
    public ResponseEntity<ApiResponse<MediaFileResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(mediaService.findById(id)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR','VIEWER')")
    @Operation(summary = "List media files")
    public ResponseEntity<ApiResponse<PagedResponse<MediaFileResponse>>> findAll(
            @RequestParam(required = false) TipoMedia tipo,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                PagedResponse.of(mediaService.findAll(tipo, pageable))));
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR','VIEWER')")
    @Operation(summary = "Download media file")
    public ResponseEntity<InputStreamResource> download(@PathVariable UUID id) {
        MediaFileResponse meta = mediaService.findById(id);
        InputStream stream = mediaService.download(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + meta.originalName() + "\"")
                .contentType(MediaType.parseMediaType(meta.mimeType()))
                .body(new InputStreamResource(stream));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete media file")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        mediaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
