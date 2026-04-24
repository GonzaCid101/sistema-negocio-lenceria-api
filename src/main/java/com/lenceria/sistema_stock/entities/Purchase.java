package com.lenceria.sistema_stock.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "purchases")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime date;

    //Total final de la compra
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoPago paymentMethod; // "EFECTIVO", "TARJETA", "TRANSFERENCIA", "CUENTA_CORRIENTE"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchaseStatus status = PurchaseStatus.PENDIENTE; // Por defecto PENDIENTE

    @Column(name = "supplier", nullable = false)
    private String supplier = "General"; // Proveedor de la compra

    @Column(name = "invoice_number")
    private String invoiceNumber; // Número de factura del proveedor

    //Relacion 1-*
    //Cascade: guardo solo la compra y se guardan los detalles automaticamente.
    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseDetail> details = new ArrayList<>();

    // Relación con los pagos parciales
    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchasePayment> payments = new ArrayList<>();

    //---CONSTRUCTORES---
    public Purchase(){
    }

    public Purchase(MetodoPago paymentMethod){
        this.date = LocalDateTime.now();
        this.paymentMethod = paymentMethod;
        this.totalAmount = BigDecimal.ZERO; //Por defecto en cero
        this.status = PurchaseStatus.PENDIENTE;
        this.supplier = "General";
    }

    // --- GETTERS Y SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public MetodoPago getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(MetodoPago paymentMethod) { this.paymentMethod = paymentMethod; }

    public PurchaseStatus getStatus() { return status; }
    public void setStatus(PurchaseStatus status) { this.status = status; }

    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public List<PurchaseDetail> getDetails() { return details; }
    public void setDetails(List<PurchaseDetail> details) { this.details = details; }

    public List<PurchasePayment> getPayments() { return payments; }
    public void setPayments(List<PurchasePayment> payments) { this.payments = payments; }

    //Metodo extra para sumar plata al total a medida que se agregan prendas.
    public void addAmount(BigDecimal amount){
        this.totalAmount = totalAmount.add(amount);
    }

    // Metodo para calcular el monto pendiente de pago
    public BigDecimal getPendingAmount() {
        BigDecimal pagado = payments.stream()
                .map(PurchasePayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalAmount.subtract(pagado);
    }

    // Metodo para agregar un pago
    public void addPayment(PurchasePayment payment) {
        payments.add(payment);
        payment.setPurchase(this);
    }
}
