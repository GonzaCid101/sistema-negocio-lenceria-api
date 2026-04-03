package com.lenceria.sistema_stock.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lenceria.sistema_stock.entities.Variant;



@Repository
public interface VariantRepository extends JpaRepository<Variant,Long>{
    Variant findByBarCode(String barCode);
}
