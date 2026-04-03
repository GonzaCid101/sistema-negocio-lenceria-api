package com.lenceria.sistema_stock.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lenceria.sistema_stock.dtos.ArticleDTO;
import com.lenceria.sistema_stock.entities.Article;
import com.lenceria.sistema_stock.entities.ArticleCategory;
import com.lenceria.sistema_stock.entities.Brand;
import com.lenceria.sistema_stock.entities.Variant;
import com.lenceria.sistema_stock.repositories.ArticleCategoryRepository;
import com.lenceria.sistema_stock.repositories.ArticleRepository;
import com.lenceria.sistema_stock.repositories.BrandRepository;
import com.lenceria.sistema_stock.repositories.VariantRepository;

import jakarta.transaction.Transactional;

@Service
public class ArticleService {
    private final ArticleCategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ArticleRepository articleRepository;
    private final VariantRepository variantRepository;

    public ArticleService(BrandRepository brandRepository, ArticleCategoryRepository categoryRepository, ArticleRepository articleRepository, VariantRepository variantRepository){
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.articleRepository = articleRepository;
        this.variantRepository = variantRepository;
    }

    public List<Article> obtenerTodos() {
        return articleRepository.findByActive(true); //Busca los activos solo
    }

    public Article createArticle(ArticleDTO articleDTO){

        Brand marcaEncontrada = brandRepository.findById(articleDTO.getBrandId()).orElseThrow(() -> new RuntimeException("¡Error! La marca ID:" + articleDTO.getBrandId() + " no existe."));
        ArticleCategory categoriaEncontrada = categoryRepository.findById(articleDTO.getCategoryId()).orElseThrow(() -> new RuntimeException("¡Error! La categoria ID" + articleDTO.getCategoryId() + " no existe."));

        if(!marcaEncontrada.getActive() || !categoriaEncontrada.getActive()) {
            throw new RuntimeException("La marca o categoria fueron borradas.");
        }

        Article newArticle = new Article();
        newArticle.setName(articleDTO.getName());
        newArticle.setDescription(articleDTO.getDescription());
        newArticle.setBrand(marcaEncontrada);
        newArticle.setCategory(categoriaEncontrada);
        newArticle.setActive(true);

        return articleRepository.save(newArticle);
    }

    @Transactional
    public Article updateArticle(Long id, ArticleDTO updatedArticle){
        Article originalArticle = articleRepository.findById(id).orElseThrow(() -> new RuntimeException("Articulo no encontrado."));
        
        Brand marcaEncontrada = brandRepository.findById(updatedArticle.getBrandId()).orElseThrow(() -> new RuntimeException("¡Error! La marca ID:" + updatedArticle.getBrandId() + " no existe."));
        ArticleCategory categoriaEncontrada = categoryRepository.findById(updatedArticle.getCategoryId()).orElseThrow(() -> new RuntimeException("¡Error! La categoria ID" + updatedArticle.getCategoryId() + " no existe."));

        if(!marcaEncontrada.getActive() || !categoriaEncontrada.getActive()) {
            throw new RuntimeException("La marca o categoria fueron borradas.");
        }
        
        if(!originalArticle.getActive()){
            throw new RuntimeException("Articulo borrado.");
        }
        originalArticle.setName(updatedArticle.getName());
        originalArticle.setDescription(updatedArticle.getDescription());
        originalArticle.setBrand(marcaEncontrada);
        originalArticle.setCategory(categoriaEncontrada);

        return originalArticle;
    }
    
    @Transactional
    public void deleteArticle(Long id) {
        Article articulo = articleRepository.findById(id).orElseThrow(() -> new RuntimeException("Articulo no encontrado."));

        articulo.setActive(false);

    }

    public Variant obtenerVariante(String code) {
        Variant variant = variantRepository.findByBarCode(code);
        if (variant != null && !variant.getActive()) {
            return null; // No devolver variantes borradas
        }
        return variant;
    }

    @Transactional
    public Variant agregarVariante(Long id, Variant variante){
        Article articleEncontrado = articleRepository.findById(id).orElseThrow(() -> new RuntimeException("¡Error! El article ID " + id + " no existe."));

        variante.setArticle(articleEncontrado);

        return variantRepository.save(variante);
    }

    @Transactional
    public void eliminarVariante( Long articleId, Long varianteId) {
        Variant varianteExistente = variantRepository.findById(varianteId).orElseThrow(() -> new RuntimeException("¡Error! La variante ID " + varianteId + " no existe."));

        if (articleId != varianteExistente.getArticle().getId()){
            throw new IllegalArgumentException("Variante o articulo incorrectos. No relacionados.");
        }
        
        if(!varianteExistente.getActive()){
            throw new RuntimeException("La variante ya fue borrada.");
        }
        
        varianteExistente.setActive(false);
    }

    @Transactional
    public void actualizarVariante(Long articleId, Long varianteId, Variant varianteActualizada) {
        
        Variant varianteExistente = variantRepository.findById(varianteId).orElseThrow(() -> new RuntimeException("Variante no encontrada"));
        
        if (articleId != varianteExistente.getArticle().getId()){
            throw new IllegalArgumentException("Variante o articulo incorrectos. No relacionados.");
        }
        varianteExistente.setSize(varianteActualizada.getSize());
        varianteExistente.setColor(varianteActualizada.getColor());
        varianteExistente.setPrice(varianteActualizada.getPrice());

        }
}
