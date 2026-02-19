package ao.gov.embaixada.si.entity;

import ao.gov.embaixada.commons.dto.BaseEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "menu_items")
public class MenuItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(name = "parent_id")
    private UUID parentId;

    @Column(name = "label_pt", nullable = false, length = 200)
    private String labelPt;

    @Column(name = "label_en", length = 200)
    private String labelEn;

    @Column(name = "label_de", length = 200)
    private String labelDe;

    @Column(name = "label_cs", length = 200)
    private String labelCs;

    @Column(length = 500)
    private String url;

    @Column(name = "page_id")
    private UUID pageId;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "open_new_tab")
    private boolean openNewTab = false;

    @Column(length = 50)
    private String icon;

    @Column(nullable = false)
    private boolean activo = true;

    public Menu getMenu() { return menu; }
    public void setMenu(Menu menu) { this.menu = menu; }

    public UUID getParentId() { return parentId; }
    public void setParentId(UUID parentId) { this.parentId = parentId; }

    public String getLabelPt() { return labelPt; }
    public void setLabelPt(String labelPt) { this.labelPt = labelPt; }

    public String getLabelEn() { return labelEn; }
    public void setLabelEn(String labelEn) { this.labelEn = labelEn; }

    public String getLabelDe() { return labelDe; }
    public void setLabelDe(String labelDe) { this.labelDe = labelDe; }

    public String getLabelCs() { return labelCs; }
    public void setLabelCs(String labelCs) { this.labelCs = labelCs; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public UUID getPageId() { return pageId; }
    public void setPageId(UUID pageId) { this.pageId = pageId; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public boolean isOpenNewTab() { return openNewTab; }
    public void setOpenNewTab(boolean openNewTab) { this.openNewTab = openNewTab; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
