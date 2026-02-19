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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PageService {

    private final PageRepository pageRepository;

    public PageService(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    public PageResponse create(PageCreateRequest request) {
        if (pageRepository.existsBySlug(request.slug())) {
            throw new DuplicateResourceException("Page with slug '" + request.slug() + "' already exists");
        }

        Page page = new Page();
        page.setSlug(request.slug());
        page.setTipo(request.tipo());
        page.setTemplate(request.template());
        page.setSortOrder(request.sortOrder() != null ? request.sortOrder() : 0);
        page.setParentId(request.parentId());
        page.setFeaturedImageId(request.featuredImageId());

        for (TranslationRequest tr : request.translations()) {
            PageTranslation translation = new PageTranslation();
            translation.setIdioma(tr.idioma());
            translation.setTitulo(tr.titulo());
            translation.setConteudo(tr.conteudo());
            translation.setExcerto(tr.excerto());
            translation.setMetaTitulo(tr.metaTitulo());
            translation.setMetaDescricao(tr.metaDescricao());
            translation.setMetaKeywords(tr.metaKeywords());
            translation.setOgImageUrl(tr.ogImageUrl());
            page.addTranslation(translation);
        }

        return toResponse(pageRepository.save(page));
    }

    @Transactional(readOnly = true)
    public PageResponse findById(UUID id) {
        return toResponse(pageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Page not found: " + id)));
    }

    @Transactional(readOnly = true)
    public PageResponse findBySlug(String slug) {
        return toResponse(pageRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Page not found: " + slug)));
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<PageResponse> findAll(
            TipoPagina tipo, EstadoConteudo estado, Pageable pageable) {
        if (tipo != null && estado != null) {
            return new org.springframework.data.domain.PageImpl<>(
                    pageRepository.findByTipoAndEstado(tipo, estado).stream().map(this::toResponse).toList());
        }
        if (tipo != null) {
            return new org.springframework.data.domain.PageImpl<>(
                    pageRepository.findByTipo(tipo).stream().map(this::toResponse).toList());
        }
        if (estado != null) {
            return new org.springframework.data.domain.PageImpl<>(
                    pageRepository.findByEstado(estado).stream().map(this::toResponse).toList());
        }
        return pageRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<PageResponse> findPublished(TipoPagina tipo) {
        if (tipo != null) {
            return pageRepository.findByTipoAndEstado(tipo, EstadoConteudo.PUBLISHED)
                    .stream().map(this::toResponse).toList();
        }
        return pageRepository.findPublished(EstadoConteudo.PUBLISHED)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<PageResponse> search(String query, Idioma idioma) {
        return pageRepository.searchByContent(query, idioma)
                .stream().map(this::toResponse).toList();
    }

    public PageResponse update(UUID id, PageUpdateRequest request) {
        Page page = pageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Page not found: " + id));

        if (request.slug() != null && !request.slug().equals(page.getSlug())) {
            if (pageRepository.existsBySlug(request.slug())) {
                throw new DuplicateResourceException("Page with slug '" + request.slug() + "' already exists");
            }
            page.setSlug(request.slug());
        }
        if (request.tipo() != null) page.setTipo(request.tipo());
        if (request.template() != null) page.setTemplate(request.template());
        if (request.sortOrder() != null) page.setSortOrder(request.sortOrder());
        if (request.parentId() != null) page.setParentId(request.parentId());
        if (request.featuredImageId() != null) page.setFeaturedImageId(request.featuredImageId());

        if (request.translations() != null) {
            for (TranslationRequest tr : request.translations()) {
                PageTranslation existing = page.getTranslations().stream()
                        .filter(t -> t.getIdioma() == tr.idioma())
                        .findFirst().orElse(null);

                if (existing != null) {
                    existing.setTitulo(tr.titulo());
                    existing.setConteudo(tr.conteudo());
                    existing.setExcerto(tr.excerto());
                    existing.setMetaTitulo(tr.metaTitulo());
                    existing.setMetaDescricao(tr.metaDescricao());
                    existing.setMetaKeywords(tr.metaKeywords());
                    existing.setOgImageUrl(tr.ogImageUrl());
                } else {
                    PageTranslation newTranslation = new PageTranslation();
                    newTranslation.setIdioma(tr.idioma());
                    newTranslation.setTitulo(tr.titulo());
                    newTranslation.setConteudo(tr.conteudo());
                    newTranslation.setExcerto(tr.excerto());
                    newTranslation.setMetaTitulo(tr.metaTitulo());
                    newTranslation.setMetaDescricao(tr.metaDescricao());
                    newTranslation.setMetaKeywords(tr.metaKeywords());
                    newTranslation.setOgImageUrl(tr.ogImageUrl());
                    page.addTranslation(newTranslation);
                }
            }
        }

        return toResponse(pageRepository.save(page));
    }

    public PageResponse updateEstado(UUID id, EstadoConteudo novoEstado) {
        Page page = pageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Page not found: " + id));

        page.setEstado(novoEstado);
        if (novoEstado == EstadoConteudo.PUBLISHED && page.getPublishedAt() == null) {
            page.setPublishedAt(Instant.now());
        }

        return toResponse(pageRepository.save(page));
    }

    public void delete(UUID id) {
        if (!pageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Page not found: " + id);
        }
        pageRepository.deleteById(id);
    }

    private PageResponse toResponse(Page page) {
        List<TranslationResponse> translations = page.getTranslations().stream()
                .map(t -> new TranslationResponse(
                        t.getId(), t.getIdioma(), t.getTitulo(), t.getConteudo(),
                        t.getExcerto(), t.getMetaTitulo(), t.getMetaDescricao(),
                        t.getMetaKeywords(), t.getOgImageUrl()))
                .toList();

        return new PageResponse(
                page.getId(), page.getSlug(), page.getTipo(), page.getEstado(),
                page.getTemplate(), page.getSortOrder(), page.getParentId(),
                page.getFeaturedImageId(), page.getPublishedAt(),
                translations, page.getCreatedAt(), page.getUpdatedAt());
    }
}
