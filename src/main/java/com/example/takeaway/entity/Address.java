/**
 * 地址实体类
 * 对应数据库 address 表，存储用户收货地址信息
 */
package com.example.takeaway.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "address")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    /** 地址ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 用户ID */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 收货人姓名 */
    @Column(nullable = false, length = 100)
    private String name;

    /** 收货人电话 */
    @Column(nullable = false, length = 20)
    private String phone;

    /** 省份 */
    @Column(name = "province", length = 50)
    private String province;

    /** 城市 */
    @Column(name = "city", length = 50)
    private String city;

    /** 区县 */
    @Column(name = "district", length = 50)
    private String district;

    /** 详细地址 */
    @Column(name = "detail", nullable = false, length = 500)
    private String detail;

    /** 是否为默认地址 */
    @Column(name = "is_default")
    private Boolean isDefault = false;

    /** 创建时间 */
    @Column(name = "created_at")
    private Long createdAt;

    /** 更新时间 */
    @Column(name = "updated_at")
    private Long updatedAt;

    /** 持久化前自动设置创建时间和更新时间 */
    @PrePersist
    public void prePersist() {
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
    }

    /** 更新前自动设置更新时间 */
    @PreUpdate
    public void preUpdate() {
        updatedAt = System.currentTimeMillis();
    }
}
