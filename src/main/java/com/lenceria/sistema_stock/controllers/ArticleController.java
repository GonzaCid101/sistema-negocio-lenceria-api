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
    public ResponseEntity<String> createArticle(@Valid @RequestBody ArticleDTO articleDTO){ //Transforma el JSON que se ingresa en un objeto article
        articleService.createArticle(articleDTO);
        return ResponseEntity.ok("Articulo creado correctamente.");
    }

    @PostMapping ("/{id}/variantes")
    public ResponseEntity<String> agregarVariante(@PathVariable Long id, @Valid @RequestBody Variant variante){
        articleService.agregarVariante(id, variante);
        return ResponseEntity.ok("Variante creada correctamente.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateArticle(@PathVariable Long id, @Valid @RequestBody ArticleDTO updatedArticle) {
        articleService.updateArticle(id, updatedArticle);
        return ResponseEntity.ok("Articulo actualizado correctamente.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.ok("Articulo eliminado correctamente.");
    }

    @DeleteMapping ("/{articuloId}/variantes/{varianteId}")
    public ResponseEntity<?> eliminarVariante(@PathVariable Long articuloId, @PathVariable Long varianteId) {
        try {
            articleService.eliminarVariante(articuloId, varianteId);
            return ResponseEntity.ok("Variante eliminada correctamente.");
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
            articleService.actualizarVariante(articuloId, varianteId, varianteActualizada);
            return ResponseEntity.ok("Variante actualizada correctamente.");
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
