package ao.gov.embaixada.si.service;

import ao.gov.embaixada.si.dto.*;
import ao.gov.embaixada.si.entity.Page;
import ao.gov.embaixada.si.entity.PageTranslation;
import ao.gov.embaixada.si.enums.EstadoConteudo;
import ao.gov.embaixada.si.enums.Idioma;
import ao.gov.embaixada.si.enums.TipoPagina;
import ao.gov.embaixada.si.exception.DuplicateResourceException;
import ao.gov.embaixada.si.exception.ResourceNotFoundException;
import ao.gov.embaixada.si.repository.PageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PageServiceTest {

    @Mock
    private PageRepository pageRepository;

    @InjectMocks
    private PageService pageService;

    private Page createPageEntity(String slug, TipoPagina tipo) {
        Page page = new Page();
        page.setId(UUID.randomUUID());
        page.setSlug(slug);
        page.setTipo(tipo);
        page.setEstado(EstadoConteudo.DRAFT);
        page.setSortOrder(0);
        page.setCreatedAt(Instant.now());
        page.setUpdatedAt(Instant.now());

        PageTranslation translation = new PageTranslation();
        translation.setId(UUID.randomUUID());
        translation.setIdioma(Idioma.PT);
        translation.setTitulo("Titulo Teste");
        translation.setConteudo("<p>Conteudo</p>");
        page.setTranslations(new ArrayList<>(List.of(translation)));
        translation.setPage(page);

        return page;
    }

    @Test
    void shouldCreatePage() {
        Page entity = createPageEntity("about-us", TipoPagina.INSTITUTIONAL);
        when(pageRepository.existsBySlug("about-us")).thenReturn(false);
        when(pageRepository.save(any(Page.class))).thenReturn(entity);

        PageCreateRequest request = new PageCreateRequest(
                "about-us", TipoPagina.INSTITUTIONAL, null, null, null, null,
                List.of(new TranslationRequest(Idioma.PT, "Titulo Teste", "<p>Conteudo</p>",
                        null, null, null, null, null)));

        PageResponse response = pageService.create(request);

        assertNotNull(response);
        assertEquals("about-us", response.slug());
        assertEquals(TipoPagina.INSTITUTIONAL, response.tipo());
        assertEquals(EstadoConteudo.DRAFT, response.estado());
        verify(pageRepository).save(any(Page.class));
    }

    @Test
    void shouldThrowDuplicateSlug() {
        when(pageRepository.existsBySlug("existing-slug")).thenReturn(true);

        PageCreateRequest request = new PageCreateRequest(
                "existing-slug", TipoPagina.NEWS, null, null, null, null,
                List.of(new TranslationRequest(Idioma.PT, "Title", null, null, null, null, null, null)));

        assertThrows(DuplicateResourceException.class, () -> pageService.create(request));
    }

    @Test
    void shouldFindById() {
        UUID id = UUID.randomUUID();
        Page entity = createPageEntity("test", TipoPagina.FAQ);
        entity.setId(id);
        when(pageRepository.findById(id)).thenReturn(Optional.of(entity));

        PageResponse response = pageService.findById(id);

        assertNotNull(response);
        assertEquals("test", response.slug());
    }

    @Test
    void shouldThrowNotFoundById() {
        UUID id = UUID.randomUUID();
        when(pageRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pageService.findById(id));
    }

    @Test
    void shouldFindBySlug() {
        Page entity = createPageEntity("embaixador", TipoPagina.INSTITUTIONAL);
        when(pageRepository.findBySlug("embaixador")).thenReturn(Optional.of(entity));

        PageResponse response = pageService.findBySlug("embaixador");

        assertEquals("embaixador", response.slug());
    }

    @Test
    void shouldThrowNotFoundBySlug() {
        when(pageRepository.findBySlug("nonexistent")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pageService.findBySlug("nonexistent"));
    }

    @Test
    void shouldFindAllPaged() {
        Page entity = createPageEntity("test", TipoPagina.NEWS);
        org.springframework.data.domain.Page<Page> pagedResult =
                new PageImpl<>(List.of(entity));
        when(pageRepository.findAll(any(Pageable.class))).thenReturn(pagedResult);

        var result = pageService.findAll(null, null, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldFindAllByTipo() {
        Page entity = createPageEntity("faq-page", TipoPagina.FAQ);
        when(pageRepository.findByTipo(TipoPagina.FAQ)).thenReturn(List.of(entity));

        var result = pageService.findAll(TipoPagina.FAQ, null, Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldFindAllByEstado() {
        Page entity = createPageEntity("published-page", TipoPagina.NEWS);
        entity.setEstado(EstadoConteudo.PUBLISHED);
        when(pageRepository.findByEstado(EstadoConteudo.PUBLISHED)).thenReturn(List.of(entity));

        var result = pageService.findAll(null, EstadoConteudo.PUBLISHED, Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldFindAllByTipoAndEstado() {
        Page entity = createPageEntity("institutional-published", TipoPagina.INSTITUTIONAL);
        entity.setEstado(EstadoConteudo.PUBLISHED);
        when(pageRepository.findByTipoAndEstado(TipoPagina.INSTITUTIONAL, EstadoConteudo.PUBLISHED))
                .thenReturn(List.of(entity));

        var result = pageService.findAll(TipoPagina.INSTITUTIONAL, EstadoConteudo.PUBLISHED, Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldUpdatePage() {
        UUID id = UUID.randomUUID();
        Page entity = createPageEntity("old-slug", TipoPagina.INSTITUTIONAL);
        entity.setId(id);
        when(pageRepository.findById(id)).thenReturn(Optional.of(entity));
        when(pageRepository.existsBySlug("new-slug")).thenReturn(false);
        when(pageRepository.save(any(Page.class))).thenReturn(entity);

        PageUpdateRequest request = new PageUpdateRequest(
                "new-slug", null, null, null, null, null, null);

        PageResponse response = pageService.update(id, request);

        assertNotNull(response);
        verify(pageRepository).save(any(Page.class));
    }

    @Test
    void shouldThrowDuplicateOnUpdateSlug() {
        UUID id = UUID.randomUUID();
        Page entity = createPageEntity("current-slug", TipoPagina.NEWS);
        entity.setId(id);
        when(pageRepository.findById(id)).thenReturn(Optional.of(entity));
        when(pageRepository.existsBySlug("taken-slug")).thenReturn(true);

        PageUpdateRequest request = new PageUpdateRequest(
                "taken-slug", null, null, null, null, null, null);

        assertThrows(DuplicateResourceException.class, () -> pageService.update(id, request));
    }

    @Test
    void shouldUpdateEstado() {
        UUID id = UUID.randomUUID();
        Page entity = createPageEntity("test", TipoPagina.NEWS);
        entity.setId(id);
        entity.setEstado(EstadoConteudo.DRAFT);
        when(pageRepository.findById(id)).thenReturn(Optional.of(entity));
        when(pageRepository.save(any(Page.class))).thenReturn(entity);

        PageResponse response = pageService.updateEstado(id, EstadoConteudo.PUBLISHED);

        assertNotNull(response);
        verify(pageRepository).save(any(Page.class));
    }

    @Test
    void shouldSetPublishedAtOnFirstPublish() {
        UUID id = UUID.randomUUID();
        Page entity = createPageEntity("test", TipoPagina.NEWS);
        entity.setId(id);
        entity.setEstado(EstadoConteudo.DRAFT);
        entity.setPublishedAt(null);
        when(pageRepository.findById(id)).thenReturn(Optional.of(entity));
        when(pageRepository.save(any(Page.class))).thenAnswer(inv -> inv.getArgument(0));

        pageService.updateEstado(id, EstadoConteudo.PUBLISHED);

        assertNotNull(entity.getPublishedAt());
    }

    @Test
    void shouldDeletePage() {
        UUID id = UUID.randomUUID();
        when(pageRepository.existsById(id)).thenReturn(true);
        doNothing().when(pageRepository).deleteById(id);

        pageService.delete(id);

        verify(pageRepository).deleteById(id);
    }

    @Test
    void shouldThrowNotFoundOnDelete() {
        UUID id = UUID.randomUUID();
        when(pageRepository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> pageService.delete(id));
    }

    @Test
    void shouldSearchPages() {
        Page entity = createPageEntity("search-result", TipoPagina.INSTITUTIONAL);
        when(pageRepository.searchByContent("angola", Idioma.PT)).thenReturn(List.of(entity));

        List<PageResponse> results = pageService.search("angola", Idioma.PT);

        assertEquals(1, results.size());
        assertEquals("search-result", results.get(0).slug());
    }
}
