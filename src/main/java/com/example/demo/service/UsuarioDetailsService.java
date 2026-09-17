package com.example.demo.service;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + correo));

        String rol = (usuario.getRol() != null && !usuario.getRol().isBlank()) 
                ? usuario.getRol().trim().toUpperCase() 
                : "CLIENTE";
        String rolFormateado = rol.startsWith("ROLE_") ? rol : "ROLE_" + rol;

        boolean isEnabled = isUsuarioActivo(usuario.getEstado());

        return new User(
                usuario.getCorreo(),
                usuario.getContrasena(),
                isEnabled,
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority(rolFormateado))
        );
    }

    private boolean isUsuarioActivo(Object estado) {
        if (estado == null) {
            return true;
        }
        String estadoStr = estado.toString().trim().toUpperCase();
        return "A".equals(estadoStr) || "ACTIVO".equals(estadoStr) || "TRUE".equals(estadoStr) || "1".equals(estadoStr);
    }
}