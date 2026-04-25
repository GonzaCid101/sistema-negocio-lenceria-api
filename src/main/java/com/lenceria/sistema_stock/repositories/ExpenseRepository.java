package com.lenceria.sistema_stock.repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lenceria.sistema_stock.entities.Expense;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.date BETWEEN :inicio AND :fin")
    BigDecimal sumarExpensasPorFecha(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT COALESCE(COUNT(e),0) FROM Expense e WHERE e.date BETWEEN :inicio AND :fin")
    Integer contarExpensasRealizadas(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT e FROM Expense e WHERE YEAR(e.date) = :anio AND MONTH(e.date) = :mes ORDER BY e.date DESC")
    List<Expense> buscarPorAnioYMes(@Param("anio") int anio, @Param("mes") int mes);

    // Devuelve las 200 expensas más recientes para mejor rendimiento
    List<Expense> findTop200ByOrderByDateDesc();
}
