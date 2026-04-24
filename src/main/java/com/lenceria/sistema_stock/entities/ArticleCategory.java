package com.lenceria.sistema_stock.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name="articleCategories")
@SQLDelete(sql = "UPDATE articleCategories SET active = false WHERE id = ?")
@SQLRestriction("active = true")
public class ArticleCategory {

    @Id //Clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Id autoincremental
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private Boolean active;

    //Lista de articulos
    // Se filtra por artículos activos gracias a @SQLRestriction en Article
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Article> articles = new ArrayList<>();

    public ArticleCategory(){
    }

    public ArticleCategory(String name, String description, Boolean active){
        this.name = name;
        this.description = description;
        this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public List<Article> getArticles() { return articles; }
    public void setArticles(List<Article> articles) { this.articles = articles; }
}
