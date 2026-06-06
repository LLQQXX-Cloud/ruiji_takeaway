/**
 * 订单实体类
 * 对应数据库 orders 表，存储订单信息
 */

package com.example.takeaway.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    // 瀹㈡埛鏄惁鍒犻櫎璁㈠崟锛堣蒋鍒犻櫎锛?    @Column(name = "user_deleted")
    private Boolean userDeleted = false;

    // 鍟嗗鏄惁鍒犻櫎璁㈠崟锛堣蒋鍒犻櫎锛?    @Column(name = "business_deleted")
    private Boolean businessDeleted = false;

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
}
