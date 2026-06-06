/**
 * 分类实体类
 * 对应数据库 category 表，存储商家商品分类信息
 */

package com.example.takeaway.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "business_id", nullable = false)
    private Long businessId;

    @Column(nullable = false, length = 50)
    private String name;

    private Integer sort;

    public Category() {
    }

    public Category(Long id, Long businessId, String name, Integer sort) {
        this.id = id;
        this.businessId = businessId;
        this.name = name;
        this.sort = sort;
    }

    @PrePersist
    protected void onCreate() {
        if (sort == null) sort = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
