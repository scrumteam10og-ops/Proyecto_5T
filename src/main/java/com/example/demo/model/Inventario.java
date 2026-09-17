package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "inventario")
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @Column(name = "referencia_producto", nullable = false, length = 50)
    private String referenciaProducto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false, length = 50)
    private String nombre;

    private String descripcion;

    @Column(name = "valor_venta", nullable = false)
    private Integer valorVenta;

    @Column(name = "valor_total", nullable = false)
    private Integer valorTotal;

    @Column(length = 1)
    private String estado = "A";

    public Inventario() {}

    // Getters y Setters
    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public String getReferenciaProducto() { return referenciaProducto; }
    public void setReferenciaProducto(String referenciaProducto) { this.referenciaProducto = referenciaProducto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getValorVenta() { return valorVenta; }
    public void setValorVenta(Integer valorVenta) { this.valorVenta = valorVenta; }

    public Integer getValorTotal() { return valorTotal; }
    public void setValorTotal(Integer valorTotal) { this.valorTotal = valorTotal; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Integer getId() {
        return idProducto;
    }
}