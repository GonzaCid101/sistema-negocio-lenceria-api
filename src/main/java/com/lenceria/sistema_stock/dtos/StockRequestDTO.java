package com.lenceria.sistema_stock.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class StockRequestDTO {
    @NotNull(message = "Debe seleccionar una variante antes")
    private Long variantId;
    @Positive(message = "La cantidad debe ser positiva")
    private Integer quantity;
    @NotBlank(message = "Debe elegir un tipo de movimiento antes")
    private String movementType;
    @NotBlank(message = "Debe ingresar una razon del movimiento antes")
    private String reason;

    // Getters y Setters
    public Long getVariantId() { return variantId; }
    public void setVariantId(Long variantId) { this.variantId = variantId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getMovementType() { return movementType; }
    public void setMovementType(String movementType) { this.movementType = movementType; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

}
