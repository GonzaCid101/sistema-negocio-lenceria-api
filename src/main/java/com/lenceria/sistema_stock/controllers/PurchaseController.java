package com.lenceria.sistema_stock.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lenceria.sistema_stock.dtos.PurchaseRequestDTO;
import com.lenceria.sistema_stock.entities.Purchase;
import com.lenceria.sistema_stock.services.PurchaseService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/compras")
public class PurchaseController {
    
    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService){
        this.purchaseService = purchaseService;
    }

    @PostMapping
    public String registrarCompra(@Valid @RequestBody PurchaseRequestDTO request){
        purchaseService.registrarCompra(request.getMetodoPago(), request.getItems());
        return "Compra registrada.";
    }

    @GetMapping
    public List<Purchase> obtenerCompras(
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Integer mes) {
        
        return purchaseService.listaCompras(anio, mes);
    }
}
