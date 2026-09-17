package com.example.demo.repository;

import com.example.demo.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findByEstado(String estado);
    List<Producto> findByNombreContainingIgnoreCaseAndEstado(String nombre, String estado);
    List<Producto> findByCategoriaIdCategoriaAndEstado(Integer idCategoria, String estado);
    List<Producto> findByNombreContainingIgnoreCaseAndCategoriaIdCategoriaAndEstado(String nombre, Integer idCategoria, String estado);
}