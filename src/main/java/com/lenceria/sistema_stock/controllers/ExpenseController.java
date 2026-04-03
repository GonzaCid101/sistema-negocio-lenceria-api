package com.lenceria.sistema_stock.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lenceria.sistema_stock.entities.Expense;
import com.lenceria.sistema_stock.services.ExpenseService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/expensas")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService){
        this.expenseService = expenseService;
    }

    @GetMapping
    public List<Expense> obtenerExpensas(
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Integer mes) {
        return expenseService.obtenerExpensas(anio, mes);
    }

    @PostMapping
    public ResponseEntity<String> crearExpensa(@RequestBody Expense expensa) {
        expenseService.crearExpensa(expensa);
        return ResponseEntity.ok("Expensa creada correctamente.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarExpensa(@PathVariable Long id, @RequestBody Expense expensaActualizada) {
        expenseService.actualizarExpensa(id, expensaActualizada);
        return ResponseEntity.ok("Expensa actualizada correctamente.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarExpensa(@PathVariable Long id) {
        expenseService.eliminarExpensa(id);
        return ResponseEntity.ok("Expensa eliminada correctamente.");
    }
}