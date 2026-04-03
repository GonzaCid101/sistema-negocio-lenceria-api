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

import com.lenceria.sistema_stock.dtos.BrandDTO;
import com.lenceria.sistema_stock.entities.Brand;
import com.lenceria.sistema_stock.services.BrandService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*") //Lista de direcciones aceptadas para funcionar con el sistema.
@RestController
@RequestMapping("/api/marcas")

public class BrandController {
    private final BrandService brandService;

    public BrandController(BrandService brandService){
        this.brandService = brandService;
    }

    @GetMapping //Devuelve la lista de los Marcas
    public List<Brand> obtenerTodos() {
        return brandService.obtenerTodos();
    }

    @PostMapping//Guarda en la base de datos. Para ingresar
    public ResponseEntity<String> createBrand(@Valid @RequestBody BrandDTO brandDTO){ //Transforma el JSON que se ingresa en un objeto brand
        brandService.createBrand(brandDTO);
        return ResponseEntity.ok("Marca creada correctamente.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateBrand(@PathVariable Long id, @Valid @RequestBody BrandDTO updatedBrand) {
        brandService.updateBrand(id, updatedBrand);
        return ResponseEntity.ok("Marca actualizada correctamente.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.ok("Marca eliminada correctamente.");
    }
}
