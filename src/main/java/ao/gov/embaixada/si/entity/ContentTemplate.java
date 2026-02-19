package ao.gov.embaixada.si.entity;

import ao.gov.embaixada.commons.dto.BaseEntity;
import ao.gov.embaixada.si.enums.TipoPagina;
import jakarta.persistence.*;

@Entity
@Table(name = "content_templates")
public class ContentTemplate extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @Column(length = 300)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pagina", nullable = false, length = 20)
    private TipoPagina tipoPagina;

    @Column(name = "template_html", columnDefinition = "TEXT")
    private String templateHtml;

    @Column(name = "schema_json", columnDefinition = "TEXT")
    private String schemaJson;

    @Column(nullable = false)
    private boolean activo = true;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public TipoPagina getTipoPagina() { return tipoPagina; }
    public void setTipoPagina(TipoPagina tipoPagina) { this.tipoPagina = tipoPagina; }

    public String getTemplateHtml() { return templateHtml; }
    public void setTemplateHtml(String templateHtml) { this.templateHtml = templateHtml; }

    public String getSchemaJson() { return schemaJson; }
    public void setSchemaJson(String schemaJson) { this.schemaJson = schemaJson; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
