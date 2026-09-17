package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "talla")
public class Talla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_talla")
    private Integer idTalla;

    @Column(nullable = false, length = 10)
    private String nombre;

    // Constructores
    public Talla() {
    }

    public Talla(Integer idTalla, String nombre) {
        this.idTalla = idTalla;
        this.nombre = nombre;
    }

    // Getters y Setters
    public Integer getIdTalla() {
        return idTalla;
    }

    public void setIdTalla(Integer idTalla) {
        this.idTalla = idTalla;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}