package app.spring.web.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Table(name = "sys_menu")
public class Menu {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "menu_name", nullable = false, length = 50)
    private String menuName;
    
    @Column(name = "menu_code", unique = true, nullable = false, length = 50)
    private String menuCode;
    
    @Column(name = "parent_id")
    private Long parentId;
    
    @Column(name = "menu_url", length = 255)
    private String menuUrl;
    
    @Column(name = "menu_icon", length = 50)
    private String menuIcon;
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    @Column(name = "menu_type", nullable = false)
    private Integer menuType = 1; // 1: menu, 2: button
    
    @Column(name = "status", nullable = false)
    private Integer status = 1; // 1: active, 0: inactive
    
    @Column(name = "description", length = 255)
    private String description;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "updated_by")
    private Long updatedBy;
    
    // Transient fields for tree structure
    @Transient
    private List<Menu> children;
    
    @Transient
    private String parentName;
    
    // Constructors
    public Menu() {}
    
    public Menu(String menuName, String menuCode) {
        this.menuName = menuName;
        this.menuCode = menuCode;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getMenuName() {
        return menuName;
    }
    
    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }
    
    public String getMenuCode() {
        return menuCode;
    }
    
    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }
    
    public Long getParentId() {
        return parentId;
    }
    
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    
    public String getMenuUrl() {
        return menuUrl;
    }
    
    public void setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl;
    }
    
    public String getMenuIcon() {
        return menuIcon;
    }
    
    public void setMenuIcon(String menuIcon) {
        this.menuIcon = menuIcon;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public Integer getMenuType() {
        return menuType;
    }
    
    public void setMenuType(Integer menuType) {
        this.menuType = menuType;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Long getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
    
    public Long getUpdatedBy() {
        return updatedBy;
    }
    
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }
    
    public List<Menu> getChildren() {
        return children;
    }
    
    public void setChildren(List<Menu> children) {
        this.children = children;
    }
    
    public String getParentName() {
        return parentName;
    }
    
    public void setParentName(String parentName) {
        this.parentName = parentName;
    }
}