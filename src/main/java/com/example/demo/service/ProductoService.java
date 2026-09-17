package com.example.demo.service;

import com.example.demo.model.Producto;
import com.example.demo.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    public List<Producto> obtenerActivos() {
        return productoRepository.findByEstado("A");
    }

    public Optional<Producto> buscarPorId(Integer id) {
        return productoRepository.findById(id);
    }

    public Producto guardar(Producto producto) {
        if (producto.getEstado() == null || producto.getEstado().trim().isEmpty()) {
            producto.setEstado("A");
        }
        return productoRepository.save(producto);
    }

    public void inactivar(Integer id) {
        productoRepository.findById(id).ifPresent(p -> {
            p.setEstado("I");
            productoRepository.save(p);
        });
    }

    public void activar(Integer id) {
        productoRepository.findById(id).ifPresent(p -> {
            p.setEstado("A");
            productoRepository.save(p);
        });
    }
}