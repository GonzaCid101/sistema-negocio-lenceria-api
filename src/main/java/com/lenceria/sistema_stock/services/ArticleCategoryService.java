package com.lenceria.sistema_stock.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lenceria.sistema_stock.dtos.ArticleCategoryDTO;
import com.lenceria.sistema_stock.entities.ArticleCategory;
import com.lenceria.sistema_stock.repositories.ArticleCategoryRepository;

import jakarta.transaction.Transactional;

@Service
public class ArticleCategoryService {
    private final ArticleCategoryRepository categoryRepository;

    public ArticleCategoryService(ArticleCategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    public List<ArticleCategory> obtenerTodos() {
        return categoryRepository.findByActive(true); //Busca los activos solo
    }

    public ArticleCategory createCategory(ArticleCategoryDTO categoryDTO){

        ArticleCategory newCategory = new ArticleCategory();
        newCategory.setName(categoryDTO.getName());
        newCategory.setDescription(categoryDTO.getDescription());
        newCategory.setActive(true);

        return categoryRepository.save(newCategory);
    }

    @Transactional
    public ArticleCategory updateCategory(Long id, ArticleCategoryDTO updatedCategory){
        ArticleCategory originalCategory = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Categoria no encontrado."));
        
        originalCategory.setName(updatedCategory.getName());
        originalCategory.setDescription(updatedCategory.getDescription());

        return originalCategory;
    }
    
    @Transactional
    public void deleteCategory(Long id) {
        ArticleCategory Category = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Categoria no encontrada."));

        Category.setActive(false);
    }
}
