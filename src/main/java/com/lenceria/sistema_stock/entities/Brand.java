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

@Entity
@Table(name = "brands")

public class Brand {
    @Id //Clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Id autoincremental
    private Long id;

    @Column(nullable = false) //Para no guardar un articulo sin nombre
    private String name;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true) //MappedBy: relacion definida en la variable article de la clase Variant.
    private List<Article> articles = new ArrayList<>();

    private String description;

    private Boolean active;

    public Brand(){
    }

    public Brand(String name, String description, Boolean active){
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

    public List<Article> getArticles() { return articles; }
    public void setArticles(List<Article> articles) { this.articles = articles; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
