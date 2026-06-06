/**
 * 购物车实体类
 * 对应数据库 cart 表，存储用户购物车商品信息
 */

package com.example.takeaway.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "business_id", nullable = false)
    private Long businessId;

    @Column(name = "food_id", nullable = false)
    private Long foodId;

    private Integer quantity;

    @Transient
    private String foodName;

    @Transient
    private Double price;

    @Transient
    private String image;

    public Cart() {
    }

    public Cart(Long id, Long userId, Long businessId, Long foodId, Integer quantity) {
        this.id = id;
        this.userId = userId;
        this.businessId = businessId;
        this.foodId = foodId;
        this.quantity = quantity;
    }

    @PrePersist
    protected void onCreate() {
        if (quantity == null) quantity = 1;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
