package ao.gov.embaixada.si.service;

import ao.gov.embaixada.si.dto.PageVersionResponse;
import ao.gov.embaixada.si.entity.Page;
import ao.gov.embaixada.si.entity.PageTranslation;
import ao.gov.embaixada.si.entity.PageVersion;
import ao.gov.embaixada.si.enums.Idioma;
import ao.gov.embaixada.si.exception.ResourceNotFoundException;
import ao.gov.embaixada.si.repository.PageRepository;
import ao.gov.embaixada.si.repository.PageVersionRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PageVersionService {

    private final PageVersionRepository versionRepository;
    private final PageRepository pageRepository;

    public PageVersionService(PageVersionRepository versionRepository, PageRepository pageRepository) {
        this.versionRepository = versionRepository;
        this.pageRepository = pageRepository;
    }

    public void createSnapshot(UUID pageId, Idioma idioma, String changeSummary) {
        Page page = pageRepository.findById(pageId)
                .orElseThrow(() -> new ResourceNotFoundException("Page not found: " + pageId));

        PageTranslation translation = page.getTranslations().stream()
                .filter(t -> t.getIdioma() == idioma)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Translation not found for page " + pageId + " idioma " + idioma));

        int nextVersion = versionRepository.findMaxVersionNumber(pageId, idioma).orElse(0) + 1;

        PageVersion version = new PageVersion();
        version.setPageId(pageId);
        version.setIdioma(idioma);
        version.setVersionNumber(nextVersion);
        version.setTitulo(translation.getTitulo());
        version.setConteudo(translation.getConteudo());
        version.setExcerto(translation.getExcerto());
        version.setMetaTitulo(translation.getMetaTitulo());
        version.setMetaDescricao(translation.getMetaDescricao());
        version.setChangeSummary(changeSummary);

        versionRepository.save(version);
    }

    @Transactional(readOnly = true)
    public List<PageVersionResponse> findVersions(UUID pageId, Idioma idioma, Pageable pageable) {
        return versionRepository.findByPageIdAndIdiomaOrderByVersionNumberDesc(pageId, idioma, pageable)
                .getContent().stream().map(this::toResponse).toList();
    }

    public void restoreVersion(UUID pageId, Idioma idioma, Integer versionNumber) {
        PageVersion version = versionRepository.findByPageIdAndIdiomaAndVersionNumber(pageId, idioma, versionNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Version " + versionNumber + " not found for page " + pageId));

        Page page = pageRepository.findById(pageId)
                .orElseThrow(() -> new ResourceNotFoundException("Page not found: " + pageId));

        PageTranslation translation = page.getTranslations().stream()
                .filter(t -> t.getIdioma() == idioma)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Translation not found"));

        // Snapshot current before restoring
        createSnapshot(pageId, idioma, "Auto-snapshot before restore to v" + versionNumber);

        // Restore content
        translation.setTitulo(version.getTitulo());
        translation.setConteudo(version.getConteudo());
        translation.setExcerto(version.getExcerto());
        translation.setMetaTitulo(version.getMetaTitulo());
        translation.setMetaDescricao(version.getMetaDescricao());

        pageRepository.save(page);
    }

    private PageVersionResponse toResponse(PageVersion v) {
        return new PageVersionResponse(
                v.getId(), v.getPageId(), v.getIdioma(), v.getVersionNumber(),
                v.getTitulo(), v.getConteudo(), v.getExcerto(),
                v.getMetaTitulo(), v.getMetaDescricao(),
                v.getChangeSummary(), v.getCreatedBy(), v.getCreatedAt());
    }
}
