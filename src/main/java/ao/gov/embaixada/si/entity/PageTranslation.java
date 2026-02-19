package ao.gov.embaixada.si.entity;

import ao.gov.embaixada.commons.dto.BaseEntity;
import ao.gov.embaixada.si.enums.Idioma;
import jakarta.persistence.*;

@Entity
@Table(name = "page_translations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"page_id", "idioma"}))
public class PageTranslation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", nullable = false)
    private Page page;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 5)
    private Idioma idioma;

    @Column(nullable = false, length = 300)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String conteudo;

    @Column(length = 500)
    private String excerto;

    @Column(name = "meta_titulo", length = 160)
    private String metaTitulo;

    @Column(name = "meta_descricao", length = 320)
    private String metaDescricao;

    @Column(name = "meta_keywords", length = 500)
    private String metaKeywords;

    @Column(name = "og_image_url", length = 500)
    private String ogImageUrl;

    public Page getPage() { return page; }
    public void setPage(Page page) { this.page = page; }

    public Idioma getIdioma() { return idioma; }
    public void setIdioma(Idioma idioma) { this.idioma = idioma; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }

    public String getExcerto() { return excerto; }
    public void setExcerto(String excerto) { this.excerto = excerto; }

    public String getMetaTitulo() { return metaTitulo; }
    public void setMetaTitulo(String metaTitulo) { this.metaTitulo = metaTitulo; }

    public String getMetaDescricao() { return metaDescricao; }
    public void setMetaDescricao(String metaDescricao) { this.metaDescricao = metaDescricao; }

    public String getMetaKeywords() { return metaKeywords; }
    public void setMetaKeywords(String metaKeywords) { this.metaKeywords = metaKeywords; }

    public String getOgImageUrl() { return ogImageUrl; }
    public void setOgImageUrl(String ogImageUrl) { this.ogImageUrl = ogImageUrl; }
}
