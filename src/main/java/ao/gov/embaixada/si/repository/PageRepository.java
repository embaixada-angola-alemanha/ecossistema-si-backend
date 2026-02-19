package ao.gov.embaixada.si.repository;

import ao.gov.embaixada.si.entity.Page;
import ao.gov.embaixada.si.enums.EstadoConteudo;
import ao.gov.embaixada.si.enums.TipoPagina;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PageRepository extends JpaRepository<Page, UUID> {

    Optional<Page> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Page> findByTipo(TipoPagina tipo);

    List<Page> findByEstado(EstadoConteudo estado);

    List<Page> findByTipoAndEstado(TipoPagina tipo, EstadoConteudo estado);

    List<Page> findByParentId(UUID parentId);

    @Query("SELECT p FROM Page p WHERE p.estado = :estado ORDER BY p.sortOrder ASC, p.publishedAt DESC")
    List<Page> findPublished(@Param("estado") EstadoConteudo estado);

    @Query("SELECT p FROM Page p JOIN p.translations t WHERE t.idioma = :idioma AND " +
            "(LOWER(t.titulo) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(t.conteudo) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Page> searchByContent(@Param("search") String search, @Param("idioma") ao.gov.embaixada.si.enums.Idioma idioma);

    long countByEstado(EstadoConteudo estado);

    long countByTipo(TipoPagina tipo);
}
