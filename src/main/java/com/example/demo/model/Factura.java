package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "facturas")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura")
    private Integer idFactura;

    @OneToOne
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;

    @Column(nullable = false, insertable = false, updatable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private Integer total;

    // Constructores
    public Factura() {
    }

    public Factura(Integer idFactura, Venta venta, LocalDateTime fecha, Integer total) {
        this.idFactura = idFactura;
        this.venta = venta;
        this.fecha = fecha;
        this.total = total;
    }

    // Getters y Setters
    public Integer getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(Integer idFactura) {
        this.idFactura = idFactura;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}