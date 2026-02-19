package ao.gov.embaixada.si.entity;

import ao.gov.embaixada.commons.dto.BaseEntity;
import ao.gov.embaixada.si.enums.Idioma;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "page_versions")
public class PageVersion extends BaseEntity {

    @Column(name = "page_id", nullable = false)
    private UUID pageId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 5)
    private Idioma idioma;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

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

    @Column(name = "change_summary", length = 500)
    private String changeSummary;

    public UUID getPageId() { return pageId; }
    public void setPageId(UUID pageId) { this.pageId = pageId; }

    public Idioma getIdioma() { return idioma; }
    public void setIdioma(Idioma idioma) { this.idioma = idioma; }

    public Integer getVersionNumber() { return versionNumber; }
    public void setVersionNumber(Integer versionNumber) { this.versionNumber = versionNumber; }

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

    public String getChangeSummary() { return changeSummary; }
    public void setChangeSummary(String changeSummary) { this.changeSummary = changeSummary; }
}
