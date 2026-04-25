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

import com.lenceria.sistema_stock.dtos.ArticleDTO;
import com.lenceria.sistema_stock.entities.Article;
import com.lenceria.sistema_stock.entities.Variant;
import com.lenceria.sistema_stock.services.ArticleService;

import jakarta.validation.Valid;


@CrossOrigin(origins = "*") //Lista de direcciones aceptadas para funcionar con el sistema.
@RestController
@RequestMapping("/api/articulos")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService){
        this.articleService = articleService;
    }

    @GetMapping //Devuelve la lista de los Articulos
    public List<Article> obtenerTodos() {//SELECT * FROM articles
        return articleService.obtenerTodos();
    }

    // Variante(s) con codigo de barras - puede devolver multiples variantes si comparten el mismo codigo (distintos colores)
    @GetMapping("/codigo/{code}")
    public List<Variant> obtenerVariantesPorCodigo(@PathVariable String code) {
        return articleService.obtenerVariantesPorCodigo(code);
    }

    @PostMapping//Guarda en la base de datos. Para ingresar
    public ResponseEntity<Article> createArticle(@Valid @RequestBody ArticleDTO articleDTO){ //Transforma el JSON que se ingresa en un objeto article
        Article articuloCreado = articleService.createArticle(articleDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(articuloCreado);
    }

    @PostMapping ("/{id}/variantes")
    public ResponseEntity<Variant> agregarVariante(@PathVariable Long id, @Valid @RequestBody Variant variante){
        Variant varianteCreada = articleService.agregarVariante(id, variante);
        return ResponseEntity.status(HttpStatus.CREATED).body(varianteCreada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable Long id, @Valid @RequestBody ArticleDTO updatedArticle) {
        Article articuloActualizado = articleService.updateArticle(id, updatedArticle);
        return ResponseEntity.ok(articuloActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Article> deleteArticle(@PathVariable Long id) {
        Article articuloEliminado = articleService.deleteArticle(id);
        return ResponseEntity.ok(articuloEliminado);
    }

    @DeleteMapping ("/{articuloId}/variantes/{varianteId}")
    public ResponseEntity<?> eliminarVariante(@PathVariable Long articuloId, @PathVariable Long varianteId) {
        try {
            Variant varianteEliminada = articleService.eliminarVariante(articuloId, varianteId);
            return ResponseEntity.ok(varianteEliminada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        } catch (RuntimeException e) {
            // Errores como "La variante ya fue borrada" o "Variante no encontrada"
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }

    @PutMapping ("/{articuloId}/variantes/{varianteId}")
    public ResponseEntity<?> actualizarVariante(@PathVariable Long articuloId, @PathVariable Long varianteId, @Valid @RequestBody Variant varianteActualizada) {
        try {
            Variant varianteActualizadaResult = articleService.actualizarVariante(articuloId, varianteId, varianteActualizada);
            return ResponseEntity.ok(varianteActualizadaResult);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }
}
