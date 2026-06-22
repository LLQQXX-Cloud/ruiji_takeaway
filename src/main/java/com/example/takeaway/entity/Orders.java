/**
 * 订单实体类
 * 对应数据库 orders 表，存储订单信息
 */

package com.example.takeaway.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "business_id", nullable = false)
    private Long businessId;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, length = 50)
    private String contact;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(length = 255)
    private String remark;

    private Integer status;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    // 用户是否删除订单(软删除)
    @Column(name = "user_deleted")
    private Boolean userDeleted = false;

    // 商家是否删除订单(软删除)
    @Column(name = "business_deleted")
    private Boolean businessDeleted = false;

    // 取消者类型：0-未取消, 1-用户取消, 2-商家取消
    @Column(name = "cancelled_by")
    private Integer cancelledBy = 0;

    // 是否已评价：0-未评价, 1-已评价
    @Column(name = "reviewed")
    private Integer reviewed = 0;

    // 订单商品列表
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "order_id", nullable = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public Orders() {
    }

    public Orders(Long id, String orderNumber, Long userId, Long businessId, BigDecimal totalPrice, String address, String contact, String phone, String remark, Integer status, LocalDateTime createTime) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.businessId = businessId;
        this.totalPrice = totalPrice;
        this.address = address;
        this.contact = contact;
        this.phone = phone;
        this.remark = remark;
        this.status = status;
        this.createTime = createTime;
    }

    @PrePersist
    protected void onCreate() {
        if (status == null) status = 0;
        if (orderNumber == null) {
            orderNumber = "ORD" + System.currentTimeMillis();
        }
        createTime = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
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

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Boolean getUserDeleted() {
        return userDeleted;
    }

    public void setUserDeleted(Boolean userDeleted) {
        this.userDeleted = userDeleted;
    }

    public Boolean getBusinessDeleted() {
        return businessDeleted;
    }

    public void setBusinessDeleted(Boolean businessDeleted) {
        this.businessDeleted = businessDeleted;
    }

    public Integer getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(Integer cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public Integer getReviewed() {
        return reviewed;
    }

    public void setReviewed(Integer reviewed) {
        this.reviewed = reviewed;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
