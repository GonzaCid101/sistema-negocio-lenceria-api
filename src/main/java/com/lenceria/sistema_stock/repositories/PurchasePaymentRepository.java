package com.lenceria.sistema_stock.repositories;

import com.lenceria.sistema_stock.entities.PurchasePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface PurchasePaymentRepository extends JpaRepository<PurchasePayment, Long> {

    // Sumar todos los pagos realizados en un rango de fechas
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PurchasePayment p WHERE p.paymentDate BETWEEN :inicio AND :fin")
    BigDecimal sumarPagosPorFecha(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}
