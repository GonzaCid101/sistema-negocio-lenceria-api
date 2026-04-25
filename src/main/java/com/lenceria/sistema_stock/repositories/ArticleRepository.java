package com.lenceria.sistema_stock.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lenceria.sistema_stock.entities.Article;

@Repository //Indica que este es un componente de acceso a datos
public interface ArticleRepository extends JpaRepository<Article,Long> { //Recibe la entidad que va a manejar y el tipo de dato de su ID

    
    // Hace LEFT OUTER JOIN con brand, category y variants en una sola consulta
    //Evita problema N+1. Optimiza
    
    @EntityGraph(attributePaths = {"brand", "category", "variants"})
    List<Article> findByActive(Boolean active);
    @EntityGraph(attributePaths = {"brand", "category", "variants"})
    List<Article> findAll();
}
