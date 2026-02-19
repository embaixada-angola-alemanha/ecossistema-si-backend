package ao.gov.embaixada.si.entity;

import ao.gov.embaixada.commons.dto.BaseEntity;
import ao.gov.embaixada.si.enums.TipoMedia;
import jakarta.persistence.*;

@Entity
@Table(name = "media_files")
public class MediaFile extends BaseEntity {

    @Column(name = "file_name", nullable = false, length = 300)
    private String fileName;

    @Column(name = "original_name", nullable = false, length = 300)
    private String originalName;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoMedia tipo;

    @Column(nullable = false)
    private Long size;

    @Column(name = "bucket", nullable = false, length = 100)
    private String bucket;

    @Column(name = "object_key", nullable = false, length = 500)
    private String objectKey;

    @Column(name = "alt_pt", length = 300)
    private String altPt;

    @Column(name = "alt_en", length = 300)
    private String altEn;

    @Column(name = "alt_de", length = 300)
    private String altDe;

    @Column(name = "alt_cs", length = 300)
    private String altCs;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public TipoMedia getTipo() { return tipo; }
    public void setTipo(TipoMedia tipo) { this.tipo = tipo; }

    public Long getSize() { return size; }
    public void setSize(Long size) { this.size = size; }

    public String getBucket() { return bucket; }
    public void setBucket(String bucket) { this.bucket = bucket; }

    public String getObjectKey() { return objectKey; }
    public void setObjectKey(String objectKey) { this.objectKey = objectKey; }

    public String getAltPt() { return altPt; }
    public void setAltPt(String altPt) { this.altPt = altPt; }

    public String getAltEn() { return altEn; }
    public void setAltEn(String altEn) { this.altEn = altEn; }

    public String getAltDe() { return altDe; }
    public void setAltDe(String altDe) { this.altDe = altDe; }

    public String getAltCs() { return altCs; }
    public void setAltCs(String altCs) { this.altCs = altCs; }

    public Integer getWidth() { return width; }
    public void setWidth(Integer width) { this.width = width; }

    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }
}
