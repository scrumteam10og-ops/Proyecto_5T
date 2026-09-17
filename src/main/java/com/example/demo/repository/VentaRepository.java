package com.example.demo.repository;

import com.example.demo.model.Usuario;
import com.example.demo.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Integer> {

    // Spring Data JPA genera automáticamente la consulta SQL a partir del nombre del método
    List<Venta> findByUsuario(Usuario usuario);
}