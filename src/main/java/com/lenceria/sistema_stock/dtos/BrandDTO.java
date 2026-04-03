package com.lenceria.sistema_stock.dtos;

import jakarta.validation.constraints.NotBlank;

public class BrandDTO {
    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;
    private String description;

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}
}
