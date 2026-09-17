package com.example.demo.service;

import com.example.demo.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    
    Usuario guardarUsuario(Usuario usuario);
    
    List<Usuario> obtenerTodos();
    
    Optional<Usuario> obtenerPorId(Integer id);
    
    Optional<Usuario> obtenerPorCorreo(String correo);
    
    Usuario actualizarUsuario(Integer id, Usuario usuarioDetalles);
    
    void cambiarEstado(Integer id, String nuevoEstado);
    
    boolean existePorCorreo(String correo);
}