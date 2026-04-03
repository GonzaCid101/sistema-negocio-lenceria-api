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
    public ResponseEntity<String> crearCategoria(@Valid @RequestBody ExpenseCategoryDTO categoriaDTO){
        expenseService.crearCategoria(categoriaDTO);
        return ResponseEntity.ok("Categoria creada correctamente.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarCategoria(@PathVariable Long id, @Valid @RequestBody ExpenseCategoryDTO categoriaActualizada) {
        expenseService.actualizarCategoria(id, categoriaActualizada);
        return ResponseEntity.ok("Categoria actualizada correctamente.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarCategoria(@PathVariable Long id) {
        expenseService.eliminarCategoria(id);
        return ResponseEntity.ok("Categoria eliminada correctamente.");
    }
}
