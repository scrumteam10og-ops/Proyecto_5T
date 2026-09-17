package com.example.demo.repository;

import com.example.demo.model.ImagenProducto;
import com.example.demo.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ImagenProductoRepository extends JpaRepository<ImagenProducto, Integer> {
    List<ImagenProducto> findByInventario(Inventario inventario);
}