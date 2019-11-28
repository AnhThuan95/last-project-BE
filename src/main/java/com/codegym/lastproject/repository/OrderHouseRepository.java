package com.codegym.lastproject.repository;

import com.codegym.lastproject.model.OrderHouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderHouseRepository extends JpaRepository<OrderHouse, Long> {
    List<OrderHouse> findByHouseId(Long houseId);

    List<OrderHouse> findByTenantId(Long tenantId);
}
