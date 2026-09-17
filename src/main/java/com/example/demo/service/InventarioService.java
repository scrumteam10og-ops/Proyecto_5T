package com.example.demo.service;

import com.example.demo.model.Inventario;
import com.example.demo.repository.InventarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventarioService {

    private final InventarioRepository inventarioRepository;

    public InventarioService(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    public List<Inventario> obtenerTodos() {
        return inventarioRepository.findAll();
    }

    public List<Inventario> obtenerActivos() {
        return inventarioRepository.findByEstado("A");
    }

    public Optional<Inventario> buscarPorId(Integer id) {
        return inventarioRepository.findById(id);
    }

    public Inventario guardar(Inventario inventario) {
        if (inventario.getCantidad() != null && inventario.getValorVenta() != null) {
            inventario.setValorTotal(inventario.getCantidad() * inventario.getValorVenta());
        }
        return inventarioRepository.save(inventario);
    }

    public void inactivar(Integer id) {
        inventarioRepository.findById(id).ifPresent(i -> {
            i.setEstado("I");
            inventarioRepository.save(i);
        });
    }
}