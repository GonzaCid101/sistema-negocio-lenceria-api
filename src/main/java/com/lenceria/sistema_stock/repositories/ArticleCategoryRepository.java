package com.lenceria.sistema_stock.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lenceria.sistema_stock.entities.ArticleCategory;

@Repository
public interface ArticleCategoryRepository extends JpaRepository<ArticleCategory, Long>{
    List<ArticleCategory> findByActive(Boolean active);
}
