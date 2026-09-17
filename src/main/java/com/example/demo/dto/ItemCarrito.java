package com.example.demo.dto;

import com.example.demo.model.Inventario;

public class ItemCarrito {

    private Inventario producto;
    private Integer cantidad;
    private Integer subtotal;
    private String metodoPago; // NEQUI / EFECTIVO / TARJETA

    // Constructor vacío requerido por Spring y Java Beans
    public ItemCarrito() {}

    // Constructor con parámetros
    public ItemCarrito(Inventario producto, Integer cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.calcularSubtotal();
    }

    public ItemCarrito(Inventario producto, Integer cantidad, String metodoPago) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.metodoPago = metodoPago;
        this.calcularSubtotal();
    }

    // Retorna 'producto' para corregir el error
    public Inventario getInventario() {
        return producto;
    }

    // Método auxiliar para recalcular automáticamente el subtotal
    public void calcularSubtotal() {
        if (this.producto != null && this.producto.getValorVenta() != null && this.cantidad != null) {
            this.subtotal = this.cantidad * this.producto.getValorVenta();
        } else {
            this.subtotal = 0;
        }
    }

    // Getters y Setters
    public Inventario getProducto() {
        return producto;
    }

    public void setProducto(Inventario producto) {
        this.producto = producto;
        this.calcularSubtotal();
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
        this.calcularSubtotal();
    }

    public Integer getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Integer subtotal) {
        this.subtotal = subtotal;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
}