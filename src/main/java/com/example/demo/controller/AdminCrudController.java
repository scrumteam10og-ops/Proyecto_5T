package com.example.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Categoria;
import com.example.demo.model.Producto;
import com.example.demo.model.Usuario;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.ColorRepository;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.repository.TallaRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.ProductoService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminCrudController {

    private static final Logger log = LoggerFactory.getLogger(AdminCrudController.class);

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private TallaRepository tallaRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProductoService productoService;

    // --- DASHBOARD ADMIN ---
    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    // --- GESTIÓN DE PRODUCTOS ---
    @GetMapping("/productos")
    public String listarProductos(@RequestParam(name = "editarId", required = false) Integer editarId, Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("tallas", tallaRepository.findAll());
        model.addAttribute("colores", colorRepository.findAll());

        if (editarId != null) {
            Producto productoEditar = productoRepository.findById(editarId).orElse(new Producto());
            model.addAttribute("nuevoProducto", productoEditar);
            model.addAttribute("modoEdicion", true);
        } else {
            model.addAttribute("nuevoProducto", new Producto());
            model.addAttribute("modoEdicion", false);
        }

        return "admin/productos";
    }

    @PostMapping("/productos/guardar")
    public String guardarProducto(@ModelAttribute("nuevoProducto") Producto producto,
                                  BindingResult result,
                                  @RequestParam(name = "file", required = false) MultipartFile file,
                                  Model model) {

        // 1. Manejo de archivo si el usuario subió una nueva imagen
        if (file != null && !file.isEmpty()) {
            try {
                File uploadDir = new File("uploads");
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                String nombreImagen = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path path = Paths.get(uploadDir.getAbsolutePath(), nombreImagen);
                
                Files.write(path, file.getBytes());

                // Asigna el nombre de la imagen al producto
                producto.setImagen(nombreImagen);

            } catch (IOException e) {
                log.error("Error al guardar la imagen de producto: ", e);
            }
        } else if (producto.getIdProducto() != null) {
            // 2. Si es edición y NO subió imagen nueva, mantener la que tenía registrada
            Producto productoExistente = productoRepository.findById(producto.getIdProducto()).orElse(null);
            if (productoExistente != null && (producto.getImagen() == null || producto.getImagen().isEmpty())) {
                producto.setImagen(productoExistente.getImagen());
            }
        }

        // 3. Guardar cambios en la Base de Datos
        productoService.guardar(producto);

        return "redirect:/admin/productos";
    }

    @GetMapping("/productos/inactivar/{id}")
    public String inactivarProducto(@PathVariable("id") Integer id) {
        productoRepository.findById(id).ifPresent(p -> {
            p.setEstado("I");
            productoRepository.save(p);
        });
        return "redirect:/admin/productos";
    }

    @GetMapping("/productos/activar/{id}")
    public String activarProducto(@PathVariable("id") Integer id) {
        productoRepository.findById(id).ifPresent(p -> {
            p.setEstado("A");
            productoRepository.save(p);
        });
        return "redirect:/admin/productos";
    }

    // --- GESTIÓN DE CATEGORÍAS ---
    @GetMapping("/categorias")
    public String listarCategorias(Model model) {
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("nuevaCategoria", new Categoria());
        return "admin/categorias";
    }

    @PostMapping("/categorias/guardar")
    public String guardarCategoria(@ModelAttribute Categoria categoria) {
        categoriaRepository.save(categoria);
        return "redirect:/admin/categorias";
    }

    // --- GESTIÓN DE USUARIOS ---
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("nuevoUsuario", new Usuario());
        return "admin/usuarios";
    }

    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario) {
        if (usuario.getContrasena() != null && !usuario.getContrasena().isEmpty()) {
            usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        }
        if (usuario.getEstado() == null) {
            usuario.setEstado("A");
        }
        usuarioRepository.save(usuario);
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/usuarios/inactivar/{id}")
    public String inactivarUsuario(@PathVariable("id") Integer id) {
        usuarioRepository.findById(id).ifPresent(u -> {
            u.setEstado("I");
            usuarioRepository.save(u);
        });
        return "redirect:/admin/usuarios";
    }
}