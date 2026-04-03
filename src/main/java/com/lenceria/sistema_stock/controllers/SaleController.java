package com.lenceria.sistema_stock.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lenceria.sistema_stock.dtos.SaleRequestDTO;
import com.lenceria.sistema_stock.entities.Sale;
import com.lenceria.sistema_stock.services.SaleService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/ventas")
public class SaleController {
    
    private final SaleService saleService;

    public SaleController(SaleService saleService){
        this.saleService = saleService;
    }

    @PostMapping
    public String registrarVenta(@Valid @RequestBody SaleRequestDTO request){
        saleService.registrarVenta(request.getMetodoPago(), request.getItems(), request.getSeller());
        return "Venta registrada.";
    }

    @GetMapping
    public List<Sale> obtenerVentas(
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Integer mes) {
        
        return saleService.listaVentas(anio, mes);
    }
    

}
