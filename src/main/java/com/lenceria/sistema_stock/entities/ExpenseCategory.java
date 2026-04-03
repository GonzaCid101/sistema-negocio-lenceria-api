package com.lenceria.sistema_stock.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "categories")

public class ExpenseCategory {
    @Id //Clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Id autoincremental
    private Long id;

    @Column(nullable = false)
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Expense> expenses = new ArrayList<>();

    private Boolean active;

    public ExpenseCategory(){
    }

    public ExpenseCategory(String name, Boolean active){
        this.name = name;
        this.active = active;
    }

    public Long getId(){return id;}
    public void setId(Long id){ this.id = id; }

    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }

    public List<Expense> getExpenses() { return expenses; }
    public void setExpenses(List<Expense> expenses) { this.expenses = expenses; }

    public Boolean getActive(){ return active; }
    public void setActive(Boolean active){ this.active = active; }
}
