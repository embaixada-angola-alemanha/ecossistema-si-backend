package ao.gov.embaixada.si.entity;

import ao.gov.embaixada.commons.dto.BaseEntity;
import ao.gov.embaixada.si.enums.EstadoConteudo;
import ao.gov.embaixada.si.enums.TipoPagina;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pages")
public class Page extends BaseEntity {

    @Column(nullable = false, unique = true, length = 200)
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoPagina tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoConteudo estado = EstadoConteudo.DRAFT;

    @Column(name = "featured_image_id")
    private UUID featuredImageId;

    @Column(name = "template", length = 100)
    private String template;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "parent_id")
    private UUID parentId;

    @OneToMany(mappedBy = "page", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PageTranslation> translations = new ArrayList<>();

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public TipoPagina getTipo() { return tipo; }
    public void setTipo(TipoPagina tipo) { this.tipo = tipo; }

    public EstadoConteudo getEstado() { return estado; }
    public void setEstado(EstadoConteudo estado) { this.estado = estado; }

    public UUID getFeaturedImageId() { return featuredImageId; }
    public void setFeaturedImageId(UUID featuredImageId) { this.featuredImageId = featuredImageId; }

    public String getTemplate() { return template; }
    public void setTemplate(String template) { this.template = template; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public Instant getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Instant publishedAt) { this.publishedAt = publishedAt; }

    public UUID getParentId() { return parentId; }
    public void setParentId(UUID parentId) { this.parentId = parentId; }

    public List<PageTranslation> getTranslations() { return translations; }
    public void setTranslations(List<PageTranslation> translations) { this.translations = translations; }

    public void addTranslation(PageTranslation translation) {
        translations.add(translation);
        translation.setPage(this);
    }

    public void removeTranslation(PageTranslation translation) {
        translations.remove(translation);
        translation.setPage(null);
    }
}
