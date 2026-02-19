package ao.gov.embaixada.si.entity;

import ao.gov.embaixada.commons.dto.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "contact_info")
public class ContactInfo extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String departamento;

    @Column(length = 300)
    private String endereco;

    @Column(length = 100)
    private String cidade;

    @Column(length = 20)
    private String codigoPostal;

    @Column(length = 100)
    private String pais;

    @Column(length = 50)
    private String telefone;

    @Column(length = 50)
    private String fax;

    @Column(length = 200)
    private String email;

    @Column(name = "horario_pt", length = 500)
    private String horarioPt;

    @Column(name = "horario_en", length = 500)
    private String horarioEn;

    @Column(name = "horario_de", length = 500)
    private String horarioDe;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(nullable = false)
    private boolean activo = true;

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }
    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getFax() { return fax; }
    public void setFax(String fax) { this.fax = fax; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getHorarioPt() { return horarioPt; }
    public void setHorarioPt(String horarioPt) { this.horarioPt = horarioPt; }
    public String getHorarioEn() { return horarioEn; }
    public void setHorarioEn(String horarioEn) { this.horarioEn = horarioEn; }
    public String getHorarioDe() { return horarioDe; }
    public void setHorarioDe(String horarioDe) { this.horarioDe = horarioDe; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
