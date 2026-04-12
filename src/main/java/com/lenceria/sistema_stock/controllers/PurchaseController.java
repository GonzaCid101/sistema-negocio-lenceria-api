package com.lenceria.sistema_stock.controllers;

import java.util.List;

import com.lenceria.sistema_stock.dtos.PurchasePaymentDTO;
import com.lenceria.sistema_stock.dtos.PurchaseResponseDTO;
import com.lenceria.sistema_stock.entities.Purchase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lenceria.sistema_stock.dtos.PurchaseRequestDTO;
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
    public ResponseEntity<String> registrarCompra(@Valid @RequestBody PurchaseRequestDTO request){
        purchaseService.registrarCompra(
            request.getMetodoPago(), 
            request.getSupplier(), 
            request.getInvoiceNumber(), 
            request.getItems()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body("Compra registrada.");
    }

    @GetMapping
    public List<PurchaseResponseDTO> obtenerCompras(
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Integer mes) {

        return purchaseService.listaCompras(anio, mes);
    }

    @PostMapping("/{id}/pagos")
    public ResponseEntity<?> registrarPago(
            @PathVariable Long id,
            @Valid @RequestBody PurchasePaymentDTO paymentDTO) {
        try {
            PurchaseResponseDTO compraActualizada = new PurchaseResponseDTO(
                purchaseService.registrarPayment(id, paymentDTO)
            );
            return ResponseEntity.ok(compraActualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error: " + e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno: " + e.getMessage());
        }
    }
}
