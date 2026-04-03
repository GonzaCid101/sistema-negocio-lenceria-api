package com.lenceria.sistema_stock.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ExpenseDTO {
    
    @NotNull(message = "Seleccione una categoría")
    private Long categoryId;
    
    @Positive(message = "El monto debe ser positivo")
    private BigDecimal amount;
    
    @NotBlank(message = "Ingrese una descripción")
    private String description;
    
    private LocalDateTime date;

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
}
