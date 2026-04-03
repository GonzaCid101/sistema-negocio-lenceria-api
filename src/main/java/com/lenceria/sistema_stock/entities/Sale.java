package com.lenceria.sistema_stock.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales")

public class Sale {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime date;

    //Total final de la venta
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoPago paymentMethod; // "EFECTIVO", "TARJETA", "TRANSFERENCIA"

    @Column(nullable = false)
    private String seller;

    //Relacion 1-*
    //Cascade: guardo solo la venta y se guardan los detalles automaticamente.
    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleDetail> details = new ArrayList<>();

    //---CONSTRUCTORES---
    public Sale(){
    }

    public Sale(MetodoPago paymentMethod, String seller){
        this.date = LocalDateTime.now();
        this.paymentMethod = paymentMethod;
        this.totalAmount = BigDecimal.ZERO; //Por defecto en cero
        this.seller = seller;
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

    public List<SaleDetail> getDetails() { return details; }
    public void setDetails(List<SaleDetail> details) { this.details = details; }

    public String getSeller() { return seller; }
    public void setSeller(String seller) { this.seller = seller; }

    //Metodo extra para sumar plata al total a medida que se agregan prendas.
    public void addAmount(BigDecimal amount){
        this.totalAmount = totalAmount.add(amount);
    }
}
