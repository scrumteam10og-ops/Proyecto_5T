package com.example.demo.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Carrito;
import com.example.demo.model.Producto;
import com.example.demo.model.Usuario;
import com.example.demo.repository.CarritoRepository;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.repository.UsuarioRepository;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private final CarritoRepository carritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    public CarritoController(CarritoRepository carritoRepository, 
                             UsuarioRepository usuarioRepository, 
                             ProductoRepository productoRepository) {
        this.carritoRepository = carritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
    }

    @GetMapping
    public String verCarrito(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioRepository.findByCorreo(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                
        List<Carrito> items = carritoRepository.findByUsuario(usuario);
        
        // Corrección de tipo: mapToDouble para soportar precios en Double
        double total = items.stream()
                .mapToDouble(i -> i.getProducto().getPrecio() * i.getCantidad())
                .sum();
        
        model.addAttribute("items", items);
        model.addAttribute("total", total);
        return "carrito/index";
    }

    @PostMapping("/agregar")
    public String agregarAlCarrito(@RequestParam Integer productoId, 
                                   @RequestParam(defaultValue = "1") Integer cantidad, 
                                   @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioRepository.findByCorreo(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Verificar si el producto ya existe en el carrito del usuario
        Carrito itemExistente = carritoRepository.findByUsuario(usuario).stream()
                .filter(i -> i.getProducto().getIdProducto().equals(productoId))
                .findFirst()
                .orElse(null);

        if (itemExistente != null) {
            itemExistente.setCantidad(itemExistente.getCantidad() + cantidad);
            carritoRepository.save(itemExistente);
        } else {
            Carrito nuevoItem = new Carrito();
            nuevoItem.setUsuario(usuario);
            nuevoItem.setProducto(producto);
            nuevoItem.setCantidad(cantidad);
            carritoRepository.save(nuevoItem);
        }

        return "redirect:/carrito";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarDelCarrito(@PathVariable Integer id) {
        carritoRepository.deleteById(id);
        return "redirect:/carrito";
    }
}