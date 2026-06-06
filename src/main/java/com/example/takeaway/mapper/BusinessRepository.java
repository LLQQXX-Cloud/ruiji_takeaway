package com.example.takeaway.mapper;

import com.example.takeaway.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {
    List<Business> findByStatus(Integer status);
    List<Business> findByNameContaining(String name);
    Business findByUsername(String username);
    List<Business> findByCategoryId(Long categoryId);
    List<Business> findByStatusAndNameContaining(Integer status, String name);
    List<Business> findByStatusAndCategoryId(Integer status, Long categoryId);
    List<Business> findByStatusAndNameContainingAndCategoryId(Integer status, String name, Long categoryId);
    List<Business> findByNameContainingAndCategoryId(String name, Long categoryId);
}
