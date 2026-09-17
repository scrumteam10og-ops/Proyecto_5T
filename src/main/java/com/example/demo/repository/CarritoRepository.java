package com.example.demo.repository;

import com.example.demo.model.Carrito;
import com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CarritoRepository extends JpaRepository<Carrito, Integer> {
    List<Carrito> findByUsuario(Usuario usuario);
    void deleteByUsuario(Usuario usuario);
}