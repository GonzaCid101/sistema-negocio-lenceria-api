package com.lenceria.sistema_stock.repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lenceria.sistema_stock.entities.Purchase;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    @Query("SELECT COALESCE(SUM(p.totalAmount), 0) FROM Purchase p WHERE p.date BETWEEN :inicio AND :fin")
    BigDecimal sumarEgresosPorFecha(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT COALESCE(COUNT(p),0) FROM Purchase p WHERE p.date BETWEEN :inicio AND :fin")
    Long comprasRealizadas(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT p FROM Purchase p WHERE YEAR(p.date) = :anio AND MONTH(p.date) = :mes ORDER BY p.date DESC")
    List<Purchase> buscarPorAnioYMes(@Param("anio") int anio, @Param("mes") int mes);

    // Busca compras entre dos fechas
    List<Purchase> findByDateBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);
}
