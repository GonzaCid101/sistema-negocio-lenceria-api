package com.lenceria.sistema_stock.entities;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity // Convierte a tabla
@Table(name="articles") // Nombre de la tabla

public class Article {

    @Id //Clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Id autoincremental
    private Long id;

    @Column(nullable = false) //Para no guardar un articulo sin nombre
    private String name;

    @ManyToOne(fetch = FetchType.LAZY) // *-1
    @JoinColumn(name = "brand_id", nullable = false) //Nombre de la clave foranea
    @JsonIgnoreProperties("articles")
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY) // *-1
    @JoinColumn(name = "article_category_id", nullable = false) //Nombre de la clave foranea
    @JsonIgnoreProperties("articles")
    private ArticleCategory category;

    private String description;

    private Boolean active;

    //Lista de variantes. 1-*
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true) //MappedBy: relacion definida en la variable article de la clase Variant.
    private List<Variant> variants = new ArrayList<>();

    // ---CONSTRUCTORES---
    public Article(){
    }

    public Article(String name, Brand brand, ArticleCategory category, String description, Boolean active){
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.description = description;
        this.active = active;
    }

    // ---GETTERS Y SETTERS---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Brand getBrand() { return brand; }
    public void setBrand(Brand brand) { this.brand = brand; }

    public ArticleCategory getCategory() { return category; }
    public void setCategory(ArticleCategory category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Variant> getVariants() { return variants; }
    public void setVariants(List<Variant> variants) { this.variants = variants; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
