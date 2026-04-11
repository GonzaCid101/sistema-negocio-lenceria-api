package com.lenceria.sistema_stock.entities;
import java.math.BigDecimal; //Para el dinero

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "variants")

public class Variant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Relacion con la clase article
    @ManyToOne(fetch = FetchType.LAZY) // *-1
    @JoinColumn(name = "article_id", nullable = false) //Nombre de la clave foranea
    @JsonIgnoreProperties("variants")
    private Article article;

    @Column(nullable = false)
    private String size;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

	@Column
	private String barCode;

    private Integer stock = 0;

    @Column(nullable = false)
    private Boolean active = true;

    // --CONSTRUCTORES---

    public Variant(){
    }

    public Variant(Article article, String size, String color, BigDecimal price, String barCode){
        this.article = article;
        this.size = size;
        this.color = color;
        this.price = price;
        this.barCode = barCode;
    }

    public String getArticleName(){
        return this.article.getName();
    }

    // --- GETTERS Y SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Article getArticle() { return article; }
    public void setArticle(Article article) { this.article = article; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getBarCode() { return barCode; }
    public void setBarCode(String barCode) { this.barCode = barCode; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}


