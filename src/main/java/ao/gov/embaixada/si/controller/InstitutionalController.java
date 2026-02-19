package ao.gov.embaixada.si.controller;

import ao.gov.embaixada.commons.dto.ApiResponse;
import ao.gov.embaixada.commons.dto.PagedResponse;
import ao.gov.embaixada.si.dto.ContactInfoResponse;
import ao.gov.embaixada.si.dto.EventResponse;
import ao.gov.embaixada.si.dto.PageResponse;
import ao.gov.embaixada.si.enums.EstadoConteudo;
import ao.gov.embaixada.si.service.ContactInfoService;
import ao.gov.embaixada.si.service.EventService;
import ao.gov.embaixada.si.service.PageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

/**
 * Public-facing API for institutional content.
 * All endpoints are under /api/v1/public/institutional — no authentication required.
 *
 * Institutional pages use the CMS Page system with well-known slugs:
 *   - embaixador (Ambassador)
 *   - sobre-angola (About Angola parent)
 *   - sobre-angola/presidente, sobre-angola/poderes, sobre-angola/geografia,
 *     sobre-angola/historia, sobre-angola/demografia, sobre-angola/economia,
 *     sobre-angola/cultura, sobre-angola/simbolos
 *   - relacoes-bilaterais (Bilateral relations)
 */
@RestController
@RequestMapping("/api/v1/public/institutional")
@Tag(name = "Institutional", description = "Public institutional content API")
public class InstitutionalController {

    private final PageService pageService;
    private final EventService eventService;
    private final ContactInfoService contactInfoService;

    public InstitutionalController(PageService pageService,
                                   EventService eventService,
                                   ContactInfoService contactInfoService) {
        this.pageService = pageService;
        this.eventService = eventService;
        this.contactInfoService = contactInfoService;
    }

    // ── Ambassador ──────────────────────────────────────────────────────

    @GetMapping("/embaixador")
    @Operation(summary = "Get Ambassador page")
    public ResponseEntity<ApiResponse<PageResponse>> getAmbassador() {
        return ResponseEntity.ok(ApiResponse.success(pageService.findBySlug("embaixador")));
    }

    // ── About Angola ────────────────────────────────────────────────────

    @GetMapping("/sobre-angola")
    @Operation(summary = "Get About Angola overview page")
    public ResponseEntity<ApiResponse<PageResponse>> getAboutAngola() {
        return ResponseEntity.ok(ApiResponse.success(pageService.findBySlug("sobre-angola")));
    }

    @GetMapping("/sobre-angola/{subsection}")
    @Operation(summary = "Get About Angola subsection",
            description = "Available subsections: presidente, poderes, geografia, historia, demografia, economia, cultura, simbolos")
    public ResponseEntity<ApiResponse<PageResponse>> getAboutAngolaSubsection(
            @PathVariable String subsection) {
        return ResponseEntity.ok(ApiResponse.success(
                pageService.findBySlug("sobre-angola/" + subsection)));
    }

    // ── Bilateral Relations ─────────────────────────────────────────────

    @GetMapping("/relacoes-bilaterais")
    @Operation(summary = "Get bilateral relations page")
    public ResponseEntity<ApiResponse<PageResponse>> getBilateralRelations() {
        return ResponseEntity.ok(ApiResponse.success(
                pageService.findBySlug("relacoes-bilaterais")));
    }

    // ── Events / Protocol ───────────────────────────────────────────────

    @GetMapping("/events")
    @Operation(summary = "List published events", description = "Paginated list of published events")
    public ResponseEntity<ApiResponse<PagedResponse<EventResponse>>> getPublishedEvents(
            @RequestParam(required = false) String tipo,
            @PageableDefault(size = 12) Pageable pageable) {
        if (tipo != null) {
            return ResponseEntity.ok(ApiResponse.success(
                    PagedResponse.of(eventService.findByTipo(tipo, pageable))));
        }
        return ResponseEntity.ok(ApiResponse.success(
                PagedResponse.of(eventService.findAll(EstadoConteudo.PUBLISHED, pageable))));
    }

    @GetMapping("/events/upcoming")
    @Operation(summary = "List upcoming events", description = "Future published events ordered by date ascending")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getUpcomingEvents() {
        return ResponseEntity.ok(ApiResponse.success(eventService.findUpcoming()));
    }

    @GetMapping("/events/calendar")
    @Operation(summary = "Events by date range", description = "Published events within a date range for calendar view")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getEventsByDateRange(
            @RequestParam Instant start, @RequestParam Instant end) {
        return ResponseEntity.ok(ApiResponse.success(eventService.findByDateRange(start, end)));
    }

    @GetMapping("/events/{id}")
    @Operation(summary = "Get event detail")
    public ResponseEntity<ApiResponse<EventResponse>> getEventById(
            @PathVariable java.util.UUID id) {
        return ResponseEntity.ok(ApiResponse.success(eventService.findById(id)));
    }

    // ── Contacts ────────────────────────────────────────────────────────

    @GetMapping("/contacts")
    @Operation(summary = "List active contacts", description = "Active contact information ordered by sort order")
    public ResponseEntity<ApiResponse<List<ContactInfoResponse>>> getActiveContacts() {
        return ResponseEntity.ok(ApiResponse.success(contactInfoService.findActive()));
    }
}
