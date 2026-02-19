package ao.gov.embaixada.si.entity;

import ao.gov.embaixada.commons.dto.BaseEntity;
import ao.gov.embaixada.si.enums.EstadoConteudo;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "events")
public class Event extends BaseEntity {

    @Column(nullable = false, length = 300)
    private String tituloPt;

    @Column(length = 300)
    private String tituloEn;

    @Column(length = 300)
    private String tituloDe;

    @Column(length = 300)
    private String tituloCs;

    @Column(name = "descricao_pt", columnDefinition = "TEXT")
    private String descricaoPt;

    @Column(name = "descricao_en", columnDefinition = "TEXT")
    private String descricaoEn;

    @Column(name = "descricao_de", columnDefinition = "TEXT")
    private String descricaoDe;

    @Column(name = "descricao_cs", columnDefinition = "TEXT")
    private String descricaoCs;

    @Column(name = "local_pt", length = 300)
    private String localPt;

    @Column(name = "local_en", length = 300)
    private String localEn;

    @Column(name = "data_inicio", nullable = false)
    private Instant dataInicio;

    @Column(name = "data_fim")
    private Instant dataFim;

    @Column(name = "image_id")
    private UUID imageId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoConteudo estado = EstadoConteudo.DRAFT;

    @Column(name = "tipo_evento", length = 50)
    private String tipoEvento;

    public String getTituloPt() { return tituloPt; }
    public void setTituloPt(String tituloPt) { this.tituloPt = tituloPt; }
    public String getTituloEn() { return tituloEn; }
    public void setTituloEn(String tituloEn) { this.tituloEn = tituloEn; }
    public String getTituloDe() { return tituloDe; }
    public void setTituloDe(String tituloDe) { this.tituloDe = tituloDe; }
    public String getTituloCs() { return tituloCs; }
    public void setTituloCs(String tituloCs) { this.tituloCs = tituloCs; }
    public String getDescricaoPt() { return descricaoPt; }
    public void setDescricaoPt(String descricaoPt) { this.descricaoPt = descricaoPt; }
    public String getDescricaoEn() { return descricaoEn; }
    public void setDescricaoEn(String descricaoEn) { this.descricaoEn = descricaoEn; }
    public String getDescricaoDe() { return descricaoDe; }
    public void setDescricaoDe(String descricaoDe) { this.descricaoDe = descricaoDe; }
    public String getDescricaoCs() { return descricaoCs; }
    public void setDescricaoCs(String descricaoCs) { this.descricaoCs = descricaoCs; }
    public String getLocalPt() { return localPt; }
    public void setLocalPt(String localPt) { this.localPt = localPt; }
    public String getLocalEn() { return localEn; }
    public void setLocalEn(String localEn) { this.localEn = localEn; }
    public Instant getDataInicio() { return dataInicio; }
    public void setDataInicio(Instant dataInicio) { this.dataInicio = dataInicio; }
    public Instant getDataFim() { return dataFim; }
    public void setDataFim(Instant dataFim) { this.dataFim = dataFim; }
    public UUID getImageId() { return imageId; }
    public void setImageId(UUID imageId) { this.imageId = imageId; }
    public EstadoConteudo getEstado() { return estado; }
    public void setEstado(EstadoConteudo estado) { this.estado = estado; }
    public String getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }
}
