package ao.gov.embaixada.si.repository;

import ao.gov.embaixada.si.entity.PageTranslation;
import ao.gov.embaixada.si.enums.Idioma;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PageTranslationRepository extends JpaRepository<PageTranslation, UUID> {

    Optional<PageTranslation> findByPageIdAndIdioma(UUID pageId, Idioma idioma);
}
