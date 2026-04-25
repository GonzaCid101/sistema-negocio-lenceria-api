package com.lenceria.sistema_stock.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lenceria.sistema_stock.dtos.StockRequestDTO;
import com.lenceria.sistema_stock.entities.StockMovement;
import com.lenceria.sistema_stock.services.StockMovementService;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/stock")
public class StockController {
    private final StockMovementService stockService;

    public StockController(StockMovementService stockService){
        this.stockService = stockService;
    }

    @PostMapping
    public ResponseEntity<StockMovement> registrarMovimiento(@RequestBody StockRequestDTO request){

        StockMovement movimientoCreado = stockService.registrarMovimiento(request.getVariantId(), request.getMovementType(), request.getQuantity(), request.getReason());

        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoCreado);
    }

    @GetMapping
    public List<StockMovement> obtenerMovimientos(
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Integer mes) {

        return stockService.listaMovimientos(anio, mes);
    }

}
