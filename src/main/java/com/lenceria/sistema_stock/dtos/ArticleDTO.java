package com.lenceria.sistema_stock.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ArticleDTO {

    @NotBlank(message="El nombre no puede estar vacío")
    private String name;
    @NotNull(message="Debes seleccionar una marca")
    private Long brandId;
    @NotNull(message = "Debes seleccionar una categoria")
    private Long categoryId;
    private String description;

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public Long getBrandId() {return brandId;}
    public void setBrandId(Long brandId) {this.brandId = brandId;}

    public Long getCategoryId() {return categoryId;}
    public void setCategoryId(Long categoryId) {this.categoryId = categoryId;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}
}
