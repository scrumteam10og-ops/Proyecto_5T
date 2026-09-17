package com.example.demo.repository;

import com.example.demo.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InventarioRepository extends JpaRepository<Inventario, Integer> {
    List<Inventario> findByEstado(String estado);
    List<Inventario> findByNombreContainingIgnoreCaseAndEstado(String nombre, String estado);
    List<Inventario> findByCategoriaIdCategoriaAndEstado(Integer idCategoria, String estado);
    List<Inventario> findByNombreContainingIgnoreCaseAndCategoriaIdCategoriaAndEstado(String nombre, Integer idCategoria, String estado);
}