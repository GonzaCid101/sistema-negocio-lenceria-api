package com.lenceria.sistema_stock.repositories;

import com.lenceria.sistema_stock.entities.Sale;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s WHERE s.date BETWEEN :inicio AND :fin")
    BigDecimal sumarIngresosPorFecha(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT COALESCE(COUNT(s),0) FROM Sale s WHERE s.date BETWEEN :inicio AND :fin")
    Long ventasRealizadas(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT s FROM Sale s WHERE YEAR(s.date) = :anio AND MONTH(s.date) = :mes ORDER BY s.date DESC")
    List<Sale> buscarPorAnioYMes(@Param("anio") int anio, @Param("mes") int mes);

    // Busca ventas entre dos fechas
    List<Sale> findByDateBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);

    List<Sale> findTop500ByOrderByDateDesc();
}
