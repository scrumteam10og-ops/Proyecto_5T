package com.example.demo.repository;

import com.example.demo.model.CompraInicial;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface CompraInicialRepository extends JpaRepository<CompraInicial, Integer> {

    // Buscar compras por la referencia exacta del producto
    List<CompraInicial> findByReferenciaProducto(String referenciaProducto);

    // Buscar compras por coincidencia en el nombre del producto
    List<CompraInicial> findByNombreContainingIgnoreCase(String nombre);
}