package com.lenceria.sistema_stock.entities;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "stock_movements")

public class StockMovement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Relacion con la variante especifica
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id",nullable = false)
    private Variant variant;

    @Column(nullable = false)
    private String movementType; //INPUT, OUTPUT

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String reason;

    //---CONSTRUCTORES---

    public StockMovement(){
    }

    public StockMovement(Variant variant, String movementType, Integer quantity, String reason){
        this.variant = variant;
        this.movementType = movementType;
        this.quantity = quantity;
        this.createdAt = LocalDateTime.now();
        this.reason = reason;

    }

    // --- GETTERS Y SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Variant getVariant() { return variant; }
    public void setVariant(Variant variant) { this.variant = variant; }

    public String getMovementType() { return movementType; }
    public void setMovementType(String movementType) { this.movementType = movementType; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
