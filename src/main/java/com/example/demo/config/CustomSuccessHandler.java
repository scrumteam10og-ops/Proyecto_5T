package com.example.demo.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 1. Verificar si el usuario tiene rol de Administrador
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equalsIgnoreCase("ROLE_ADMIN") || role.equalsIgnoreCase("ADMIN"));

        // 2. Verificar si el usuario tiene rol de Cliente
        boolean isCliente = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equalsIgnoreCase("ROLE_CLIENTE") || role.equalsIgnoreCase("CLIENTE"));

        // 3. Redireccionar según el rol correspondientes
        if (isAdmin) {
            response.sendRedirect("/admin/dashboard");
        } else if (isCliente) {
            response.sendRedirect("/catalogo"); // Redirige a la tienda / catálogo del cliente
        } else {
            response.sendRedirect("/"); // Ruta por defecto en caso de no tener rol asignado
        }
    }
}