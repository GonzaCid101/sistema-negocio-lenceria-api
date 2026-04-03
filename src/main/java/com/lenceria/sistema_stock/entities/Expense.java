package com.lenceria.sistema_stock.entities;

import java.math.BigDecimal;
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
@Table(name = "Expenses")

public class Expense {
    @Id //Clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Id autoincremental
    private Long id;

    // *-1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ExpenseCategory category;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime date;

    private String description;

    // CONSTRUCTORES
    public Expense() {
    }

    public Expense(ExpenseCategory category, String description){
        this.category = category;
        this.amount = BigDecimal.ZERO;
        this.description = description;
        this.date = LocalDateTime.now();
    }

    // --- GETTERS Y SETTERS ---

    public Long getId(){ return id; }
    public void setId(Long id){ this.id = id; }

    public ExpenseCategory getCategory(){ return category; }
    public void setCategory(ExpenseCategory category){ this.category = category; }

    public BigDecimal getAmount(){ return amount; }
    public void setAmount(BigDecimal amount){ this.amount = amount; }

    public String getDescription(){ return description; }
    public void setDescription(String description){ this.description = description; }

    public LocalDateTime getDate(){ return date; }
    public void setDate(LocalDateTime date){ this.date = date; }


}