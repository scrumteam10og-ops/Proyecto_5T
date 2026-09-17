package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    private String nombre;

    @Size(max = 55, message = "La descripción no puede exceder 55 caracteres")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    private Integer precio;

    @Column(length = 1)
    private String estado = "A";

    @Transient
    private String imagen;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @ManyToMany
    @JoinTable(
        name = "producto_talla",
        joinColumns = @JoinColumn(name = "id_producto"),
        inverseJoinColumns = @JoinColumn(name = "id_talla")
    )
    private List<Talla> tallas;

    @ManyToMany
    @JoinTable(
        name = "producto_color",
        joinColumns = @JoinColumn(name = "id_producto"),
        inverseJoinColumns = @JoinColumn(name = "id_color")
    )
    private List<Color> colores;
}