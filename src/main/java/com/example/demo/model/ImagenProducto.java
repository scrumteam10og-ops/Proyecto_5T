package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "imagenes_producto")
public class ImagenProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_imagen")
    private Integer idImagen;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Inventario inventario;

    @Column(name = "ruta_imagen", nullable = false, length = 255)
    private String rutaImagen;

    // Constructores
    public ImagenProducto() {
    }

    public ImagenProducto(Integer idImagen, Inventario inventario, String rutaImagen) {
        this.idImagen = idImagen;
        this.inventario = inventario;
        this.rutaImagen = rutaImagen;
    }

    // Getters y Setters
    public Integer getIdImagen() {
        return idImagen;
    }

    public void setIdImagen(Integer idImagen) {
        this.idImagen = idImagen;
    }

    public Inventario getInventario() {
        return inventario;
    }

    public void setInventario(Inventario inventario) {
        this.inventario = inventario;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }
}