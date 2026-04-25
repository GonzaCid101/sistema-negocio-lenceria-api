package com.lenceria.sistema_stock.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lenceria.sistema_stock.entities.StockMovement;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    // consulta en JPLQ
    @Query("SELECT COALESCE(SUM(m.quantity),0) FROM StockMovement m WHERE m.movementType = :tipoMov AND m.createdAt BETWEEN :inicio AND :fin AND m.reason = :razon")
    Integer calcularStock(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin, @Param("tipoMov") String tipoMov, @Param("razon") String razon);

    @Query("SELECT COALESCE(SUM(m.quantity),0) FROM StockMovement m WHERE m.movementType = :tipoMov AND m.createdAt BETWEEN :inicio AND :fin AND m.reason = :razon")
    Long calcularStockComprado(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin, @Param("tipoMov") String tipoMov, @Param("razon") String razon);

    // VERSIÓN OPTIMIZADA CON JOIN FETCH - evita problema N+1
    @Query("SELECT DISTINCT m FROM StockMovement m " +
           "JOIN FETCH m.variant v " +
           "JOIN FETCH v.article a " +
           "JOIN FETCH a.brand b " +
           "WHERE YEAR(m.createdAt) = :anio AND MONTH(m.createdAt) = :mes " +
           "ORDER BY m.createdAt DESC")
    List<StockMovement> buscarPorAnioYMes(@Param("anio") int anio, @Param("mes") int mes);

    // El Pageable genera LIMIT 200 nativo en SQL - no carga todo a memoria
    @Query("SELECT m FROM StockMovement m " +
           "JOIN FETCH m.variant v " +
           "JOIN FETCH v.article a " +
           "JOIN FETCH a.brand b " +
           "ORDER BY m.createdAt DESC")
    List<StockMovement> findAllWithAllData(Pageable pageable);

    // Busca movimientos de stock entre dos fechas
    List<StockMovement> findByCreatedAtBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);

    // Método original sin optimizar para compatibilidad
    List<StockMovement> findTop200ByOrderByCreatedAtDesc();

}
