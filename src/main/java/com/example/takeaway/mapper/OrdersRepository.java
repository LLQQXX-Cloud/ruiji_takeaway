package com.example.takeaway.mapper;

import com.example.takeaway.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
    Optional<Orders> findByOrderNumber(String orderNumber);
    List<Orders> findByUserIdOrderByCreateTimeDesc(Long userId);
    List<Orders> findByBusinessIdOrderByCreateTimeDesc(Long businessId);
    List<Orders> findByStatusOrderByCreateTimeDesc(Integer status);

    @Query("SELECT o FROM Orders o WHERE o.userId = :userId AND (o.userDeleted IS NULL OR o.userDeleted = false) ORDER BY o.createTime DESC")
    List<Orders> findByUserIdAndNotDeletedByUser(@Param("userId") Long userId);

    @Query("SELECT o FROM Orders o WHERE o.businessId = :businessId AND (o.businessDeleted IS NULL OR o.businessDeleted = false) ORDER BY o.createTime DESC")
    List<Orders> findByBusinessIdAndNotDeletedByBusiness(@Param("businessId") Long businessId);

    @Modifying
    @Query("UPDATE Orders o SET o.userDeleted = true WHERE o.id = :orderId")
    void softDeleteByUser(@Param("orderId") Long orderId);

    @Modifying
    @Query("UPDATE Orders o SET o.businessDeleted = true WHERE o.id = :orderId")
    void softDeleteByBusiness(@Param("orderId") Long orderId);
}
