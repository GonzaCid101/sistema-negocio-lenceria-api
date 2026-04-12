package com.lenceria.sistema_stock.dtos;

import java.math.BigDecimal;
import java.util.List;
import com.lenceria.sistema_stock.entities.MetodoPago;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PurchaseRequestDTO {

    @NotNull(message = "Seleccione un metodo de pago antes")
    private MetodoPago metodoPago;

    @NotBlank(message = "Ingrese el proveedor")
    private String supplier;

    private String invoiceNumber;

    @NotNull(message = "Llene el carrito de compras antes")
    private List<ItemCompraDTO> items; // ¡Una lista de objetos!

    // Getters y Setters
    public MetodoPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }

    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public List<ItemCompraDTO> getItems() { return items; }
    public void setItems(List<ItemCompraDTO> items) { this.items = items; }

    // --- SUB-CLASE ---
    public static class ItemCompraDTO {
        private Long variantId;
        @Positive(message = "La cantidad ingresada debe ser positiva")
        private Integer quantity;
        private BigDecimal unitPrice;

        public Long getVariantId() { return variantId; }
        public void setVariantId(Long variantId) { this.variantId = variantId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    }
}
