package com.lenceria.sistema_stock.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lenceria.sistema_stock.dtos.BrandDTO;
import com.lenceria.sistema_stock.entities.Brand;
import com.lenceria.sistema_stock.repositories.BrandRepository;

import jakarta.transaction.Transactional;

@Service
public class BrandService {
    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository){
        this.brandRepository = brandRepository;
    }

    public List<Brand> obtenerTodos() {
        return brandRepository.findByActive(true); //Busca los activos solo
    }

    public Brand createBrand(BrandDTO brandDTO){

        Brand newBrand = new Brand();
        newBrand.setName(brandDTO.getName());
        newBrand.setDescription(brandDTO.getDescription());
        newBrand.setActive(true);

        return brandRepository.save(newBrand);
    }

    @Transactional
    public Brand updateBrand(Long id, BrandDTO updatedBrand){
        Brand originalBrand = brandRepository.findById(id).orElseThrow(() -> new RuntimeException("Marca no encontrado."));
        
        if(!originalBrand.getActive()){
            throw new RuntimeException("Marca borrada.");
        }
        originalBrand.setName(updatedBrand.getName());
        originalBrand.setDescription(updatedBrand.getDescription());

        return originalBrand;
    }
    
    @Transactional
    public Brand deleteBrand(Long id) {
        Brand brand = brandRepository.findById(id).orElseThrow(() -> new RuntimeException("Marca no encontrada."));

        brand.setActive(false);
        return brand;
    }
}
