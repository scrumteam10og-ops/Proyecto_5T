package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "compra_inicial")
public class CompraInicial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_stock")
    private Integer idStock;

    @Column(name = "referencia_producto", nullable = false, length = 50)
    private String referenciaProducto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "valor_unitario", nullable = false)
    private Integer valorUnitario;

    @Column(name = "valor_total", nullable = false)
    private Integer valorTotal;

    @Column(name = "fecha_compra", insertable = false, updatable = false)
    private LocalDateTime fechaCompra;

    public CompraInicial() {}

    // Getters y Setters
    public Integer getIdStock() { return idStock; }
    public void setIdStock(Integer idStock) { this.idStock = idStock; }

    public String getReferenciaProducto() { return referenciaProducto; }
    public void setReferenciaProducto(String referenciaProducto) { this.referenciaProducto = referenciaProducto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(Integer valorUnitario) { this.valorUnitario = valorUnitario; }

    public Integer getValorTotal() { return valorTotal; }
    public void setValorTotal(Integer valorTotal) { this.valorTotal = valorTotal; }

    public LocalDateTime getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(LocalDateTime fechaCompra) { this.fechaCompra = fechaCompra; }
}