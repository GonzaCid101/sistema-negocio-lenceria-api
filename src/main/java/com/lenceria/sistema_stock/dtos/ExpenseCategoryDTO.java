package com.lenceria.sistema_stock.dtos;

import jakarta.validation.constraints.NotBlank;

public class ExpenseCategoryDTO {
    @NotBlank(message = "Ingrese un nombre antes")
    private String name;
    
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
}
