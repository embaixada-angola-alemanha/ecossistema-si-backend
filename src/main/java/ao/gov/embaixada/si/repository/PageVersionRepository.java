package ao.gov.embaixada.si.repository;

import ao.gov.embaixada.si.entity.PageVersion;
import ao.gov.embaixada.si.enums.Idioma;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PageVersionRepository extends JpaRepository<PageVersion, UUID> {

    Page<PageVersion> findByPageIdAndIdiomaOrderByVersionNumberDesc(UUID pageId, Idioma idioma, Pageable pageable);

    @Query("SELECT MAX(pv.versionNumber) FROM PageVersion pv WHERE pv.pageId = :pageId AND pv.idioma = :idioma")
    Optional<Integer> findMaxVersionNumber(@Param("pageId") UUID pageId, @Param("idioma") Idioma idioma);

    Optional<PageVersion> findByPageIdAndIdiomaAndVersionNumber(UUID pageId, Idioma idioma, Integer versionNumber);
}
