package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "color")
public class Color {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_color")
    private Integer idColor;

    @Column(nullable = false, length = 30)
    private String nombre;

    @Column(length = 7)
    private String hex = "#888888";

    // Constructores
    public Color() {
    }

    public Color(Integer idColor, String nombre, String hex) {
        this.idColor = idColor;
        this.nombre = nombre;
        this.hex = hex;
    }

    // Getters y Setters
    public Integer getIdColor() {
        return idColor;
    }

    public void setIdColor(Integer idColor) {
        this.idColor = idColor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }
}