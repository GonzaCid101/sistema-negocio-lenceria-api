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

import com.lenceria.sistema_stock.dtos.ArticleCategoryDTO;
import com.lenceria.sistema_stock.entities.ArticleCategory;
import com.lenceria.sistema_stock.services.ArticleCategoryService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*") //Lista de direcciones aceptadas para funcionar con el sistema.
@RestController
@RequestMapping("/api/categorias-articulos")

public class ArticleCategoryController {
    private final ArticleCategoryService categoryService;

    public ArticleCategoryController(ArticleCategoryService categoryService){
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<ArticleCategory> obtenerTodos() {
        return categoryService.obtenerTodos();
    }

    @PostMapping//Guarda en la base de datos. Para ingresar
    public ResponseEntity<ArticleCategory> createCategory(@Valid @RequestBody ArticleCategoryDTO categoryDTO){ //Transforma el JSON que se ingresa en un objeto ArticleCategory
        ArticleCategory categoriaCreada = categoryService.createCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaCreada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleCategory> updateCategory(@PathVariable Long id, @Valid @RequestBody ArticleCategoryDTO updatedCategory) {
        ArticleCategory categoriaActualizada = categoryService.updateCategory(id, updatedCategory);
        return ResponseEntity.ok(categoriaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ArticleCategory> deleteCategory(@PathVariable Long id) {
        ArticleCategory categoriaEliminada = categoryService.deleteCategory(id);
        return ResponseEntity.ok(categoriaEliminada);
    }
}
