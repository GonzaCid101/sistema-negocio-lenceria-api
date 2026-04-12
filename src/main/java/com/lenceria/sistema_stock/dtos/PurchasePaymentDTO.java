package com.lenceria.sistema_stock.dtos;

import com.lenceria.sistema_stock.entities.MetodoPago;
import com.lenceria.sistema_stock.entities.PurchasePayment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PurchasePaymentDTO {

    private Long id;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private MetodoPago paymentMethod;

    // Constructor vacío
    public PurchasePaymentDTO() {
    }

    // Constructor desde entidad
    public PurchasePaymentDTO(PurchasePayment payment) {
        this.id = payment.getId();
        this.amount = payment.getAmount();
        this.paymentDate = payment.getPaymentDate();
        this.paymentMethod = payment.getPaymentMethod();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public MetodoPago getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(MetodoPago paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
