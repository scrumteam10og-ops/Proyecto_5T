package com.example.demo.controller;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute("usuario") Usuario usuario,
                                   RedirectAttributes redirectAttributes) {

        if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "El correo electrónico ya está registrado.");
            return "redirect:/registro";
        }

        // Armar el nombre completo para la columna 'nombre'
        String pNom = usuario.getPrimerNom() != null ? usuario.getPrimerNom() : "";
        String sNom = usuario.getSegundoNom() != null ? usuario.getSegundoNom() : "";
        String pApe = usuario.getPrimerApelli() != null ? usuario.getPrimerApelli() : "";
        String sApe = usuario.getSegundoApelli() != null ? usuario.getSegundoApelli() : "";
        
        String nombreCompleto = (pNom + " " + sNom + " " + pApe + " " + sApe).replaceAll("\\s+", " ").trim();
        usuario.setNombre(nombreCompleto);

        // Encriptar clave
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));

        // Asignaciones por defecto
        if (usuario.getRol() == null || usuario.getRol().isBlank()) {
            usuario.setRol("ROLE_CLIENTE");
        }
        usuario.setEstado("A");

        usuarioRepository.save(usuario);
        return "redirect:/login?registrado";
    }
}