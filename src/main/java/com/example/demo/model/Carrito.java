package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "carrito")
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrito")
    private Integer idCarrito;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Inventario inventario;

    @ManyToOne
    @JoinColumn(name = "id_talla", nullable = false)
    private Talla talla;

    @ManyToOne
    @JoinColumn(name = "id_color", nullable = false)
    private Color color;

    @Column(nullable = false)
    private Integer cantidad;

    // Constructores
    public Carrito() {
    }

    public Carrito(Integer idCarrito, Usuario usuario, Inventario inventario, Talla talla, Color color, Integer cantidad) {
        this.idCarrito = idCarrito;
        this.usuario = usuario;
        this.inventario = inventario;
        this.talla = talla;
        this.color = color;
        this.cantidad = cantidad;
    }

    // Getters y Setters
    public Integer getIdCarrito() {
        return idCarrito;
    }

    public void setIdCarrito(Integer idCarrito) {
        this.idCarrito = idCarrito;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Inventario getInventario() {
        return inventario;
    }

    public void setInventario(Inventario inventario) {
        this.inventario = inventario;
    }

    public Talla getTalla() {
        return talla;
    }

    public void setTalla(Talla talla) {
        this.talla = talla;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}