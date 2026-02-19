package ao.gov.embaixada.si.repository;

import ao.gov.embaixada.si.entity.ContentTemplate;
import ao.gov.embaixada.si.enums.TipoPagina;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContentTemplateRepository extends JpaRepository<ContentTemplate, UUID> {

    Optional<ContentTemplate> findByNome(String nome);

    boolean existsByNome(String nome);

    List<ContentTemplate> findByTipoPaginaAndActivo(TipoPagina tipoPagina, boolean activo);

    List<ContentTemplate> findByActivo(boolean activo);
}
