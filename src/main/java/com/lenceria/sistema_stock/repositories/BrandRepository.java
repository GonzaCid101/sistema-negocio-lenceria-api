package com.lenceria.sistema_stock.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lenceria.sistema_stock.entities.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand,Long> {
    List<Brand> findByActive(Boolean active);
}
