package com.example.takeaway.mapper;

import com.example.takeaway.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserIdOrderByIsDefaultDescUpdatedAtDesc(Long userId);
    Address findByUserIdAndIsDefaultTrue(Long userId);
    int countByUserId(Long userId);
}
