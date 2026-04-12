package com.lenceria.sistema_stock.dtos;

import com.lenceria.sistema_stock.entities.MetodoPago;
import com.lenceria.sistema_stock.entities.Purchase;
import com.lenceria.sistema_stock.entities.PurchaseStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PurchaseResponseDTO {

    private Long id;
    private LocalDateTime date;
    private BigDecimal totalAmount;
    private MetodoPago paymentMethod;
    private PurchaseStatus status;
    private String supplier;
    private String invoiceNumber;
    private BigDecimal pendingAmount;
    private BigDecimal paidAmount;
    private List<PurchasePaymentDTO> payments;

    // Constructor vacío
    public PurchaseResponseDTO() {
    }

    // Constructor desde entidad
    public PurchaseResponseDTO(Purchase purchase) {
        this.id = purchase.getId();
        this.date = purchase.getDate();
        this.totalAmount = purchase.getTotalAmount();
        this.paymentMethod = purchase.getPaymentMethod();
        this.status = purchase.getStatus();
        this.supplier = purchase.getSupplier();
        this.invoiceNumber = purchase.getInvoiceNumber();
        this.pendingAmount = purchase.getPendingAmount();
        this.paidAmount = purchase.getTotalAmount().subtract(purchase.getPendingAmount());

        if (purchase.getPayments() != null) {
            this.payments = purchase.getPayments().stream()
                    .map(PurchasePaymentDTO::new)
                    .collect(Collectors.toList());
        }
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public MetodoPago getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(MetodoPago paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PurchaseStatus getStatus() {
        return status;
    }

    public void setStatus(PurchaseStatus status) {
        this.status = status;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public BigDecimal getPendingAmount() {
        return pendingAmount;
    }

    public void setPendingAmount(BigDecimal pendingAmount) {
        this.pendingAmount = pendingAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public List<PurchasePaymentDTO> getPayments() {
        return payments;
    }

    public void setPayments(List<PurchasePaymentDTO> payments) {
        this.payments = payments;
    }
}
