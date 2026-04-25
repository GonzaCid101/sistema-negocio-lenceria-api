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
import org.springframework.web.bind.annotation.RestController;

import com.lenceria.sistema_stock.dtos.ExpenseCategoryDTO;
import com.lenceria.sistema_stock.entities.ExpenseCategory;
import com.lenceria.sistema_stock.services.ExpenseService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/categorias")

public class ExpenseCategoryController {

    private final ExpenseService expenseService;

    public ExpenseCategoryController(ExpenseService expenseService){
        this.expenseService = expenseService;
    }

    @GetMapping
    public List<ExpenseCategory> obtenerTodos() {
        return expenseService.obtenerTodos();
    }

    @PostMapping
    public ResponseEntity<ExpenseCategory> crearCategoria(@Valid @RequestBody ExpenseCategoryDTO categoriaDTO){
        ExpenseCategory categoriaCreada = expenseService.crearCategoria(categoriaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaCreada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseCategory> actualizarCategoria(@PathVariable Long id, @Valid @RequestBody ExpenseCategoryDTO categoriaActualizada) {
        ExpenseCategory categoriaActualizadaResult = expenseService.actualizarCategoria(id, categoriaActualizada);
        return ResponseEntity.ok(categoriaActualizadaResult);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ExpenseCategory> eliminarCategoria(@PathVariable Long id) {
        ExpenseCategory categoriaEliminada = expenseService.eliminarCategoria(id);
        return ResponseEntity.ok(categoriaEliminada);
    }
}
