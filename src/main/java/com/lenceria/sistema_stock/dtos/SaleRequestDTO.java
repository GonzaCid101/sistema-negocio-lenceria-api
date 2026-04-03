package com.lenceria.sistema_stock.dtos;

import java.util.Map;

import com.lenceria.sistema_stock.entities.MetodoPago;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class SaleRequestDTO {
    
    @NotNull(message = "Seleccione un método de pago antes")
    private MetodoPago metodoPago;
    @NotEmpty(message = "Primero llene el carrito de venta antes")
    private Map<Long,Integer> items; //Mapa <id,cantidad>

    @NotBlank(message = "Ingrese el vendedor antes")
    private String seller;

    // Getters y Setters
    public MetodoPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }

    public Map<Long, Integer> getItems() { return items; }
    public void setItems(Map<Long, Integer> items) { this.items = items; }

    public String getSeller() { return seller; }
    public void setSeller(String seller) { this.seller = seller; }
}
