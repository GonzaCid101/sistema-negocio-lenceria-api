package com.lenceria.sistema_stock.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Expense> crearExpensa(@RequestBody Expense expensa) {
        Expense expensaCreada = expenseService.crearExpensa(expensa);
        return ResponseEntity.status(HttpStatus.CREATED).body(expensaCreada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Expense> actualizarExpensa(@PathVariable Long id, @RequestBody Expense expensaActualizada) {
        Expense expensaActualizadaResult = expenseService.actualizarExpensa(id, expensaActualizada);
        return ResponseEntity.ok(expensaActualizadaResult);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Expense> eliminarExpensa(@PathVariable Long id) {
        Expense expensaEliminada = expenseService.eliminarExpensa(id);
        return ResponseEntity.ok(expensaEliminada);
    }
}
