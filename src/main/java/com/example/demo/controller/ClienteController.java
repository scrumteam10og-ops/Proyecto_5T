package com.example.demo.controller;

import com.example.demo.dto.ItemCarrito;
import com.example.demo.model.Inventario;
import com.example.demo.repository.InventarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ClienteController {

    private final InventarioRepository inventarioRepo;

    public ClienteController(InventarioRepository inventarioRepo) {
        this.inventarioRepo = inventarioRepo;
    }

    // Método auxiliar para recuperar o inicializar el carrito en la sesión HTTP
    @SuppressWarnings("unchecked")
    private List<ItemCarrito> obtenerCarrito(HttpSession session) {
        List<ItemCarrito> carrito = (List<ItemCarrito>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute("carrito", carrito);
        }
        return carrito;
    }

    // 1. PÁGINA INICIAL / VISTA PÚBLICA DE LANDING
    @GetMapping("/")
    public String index(Model model) {
        List<Inventario> productos = inventarioRepo.findAll();
        model.addAttribute("productos", productos);
        return "index"; // Carga src/main/resources/templates/index.html
    }

    @GetMapping("/confirmacion.html")
    public String evitarConfirmacionEstatica() {
        return "redirect:/catalogo";
    }

    // 2. CATÁLOGO DEL CLIENTE CON CARRITO
    @GetMapping("/catalogo")
    public String verCatalogo(Model model, HttpSession session) {
        List<Inventario> productos = inventarioRepo.findAll();
        List<ItemCarrito> carrito = obtenerCarrito(session);

        int total = 0;
        int totalItems = 0;

        for (ItemCarrito item : carrito) {
            if (item != null && item.getSubtotal() != null) {
                total += item.getSubtotal();
            }
            if (item != null && item.getCantidad() != null) {
                totalItems += item.getCantidad();
            }
        }

        model.addAttribute("productos", productos);
        model.addAttribute("carrito", carrito);
        model.addAttribute("totalCarrito", total);
        model.addAttribute("totalItems", totalItems);

        return "cliente/catalogo"; // Carga src/main/resources/templates/cliente/catalogo.html
    }

    // 3. AGREGAR PRODUCTO AL CARRITO
    @PostMapping("/carrito/agregar")
    public String agregarAlCarrito(@RequestParam("idProducto") Integer idProducto,
                                   @RequestParam("cantidad") Integer cantidad,
                                   HttpSession session) {

        Inventario producto = inventarioRepo.findById(idProducto).orElse(null);

        if (producto != null && cantidad != null && cantidad > 0) {
            List<ItemCarrito> carrito = obtenerCarrito(session);
            boolean encontrado = false;

            for (ItemCarrito item : carrito) {
                if (item.getProducto() != null && item.getProducto().getIdProducto().equals(idProducto)) {
                    int nuevaCantidad = item.getCantidad() + cantidad;
                    if (nuevaCantidad > producto.getCantidad()) {
                        nuevaCantidad = producto.getCantidad();
                    }
                    item.setCantidad(nuevaCantidad);
                    item.setSubtotal(nuevaCantidad * producto.getValorVenta());
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                ItemCarrito nuevoItem = new ItemCarrito();
                nuevoItem.setProducto(producto);
                nuevoItem.setCantidad(cantidad);
                nuevoItem.setSubtotal(cantidad * producto.getValorVenta());
                carrito.add(nuevoItem);
            }

            session.setAttribute("carrito", carrito);
        }

        return "redirect:/catalogo";
    }

    // 4. ELIMINAR UN ITEM DEL CARRITO
    @GetMapping("/carrito/eliminar/{id}")
    public String eliminarDelCarrito(@PathVariable("id") Integer idProducto, HttpSession session) {
        List<ItemCarrito> carrito = obtenerCarrito(session);
        carrito.removeIf(item -> item.getProducto() != null && item.getProducto().getIdProducto().equals(idProducto));
        session.setAttribute("carrito", carrito);
        return "redirect:/catalogo";
    }

    // 5. VACIAR EL CARRITO COMPLETO
    @GetMapping("/carrito/vaciar")
    public String vaciarCarrito(HttpSession session) {
        session.removeAttribute("carrito");
        return "redirect:/catalogo";
    }

    // 6. PROCESAR EL PAGO Y ACTUALIZAR STOCK
    @PostMapping("/carrito/procesar")
    public String procesarPago(@RequestParam(name = "metodoPago", defaultValue = "NEQUI") String metodoPago,
                               HttpSession session,
                               Model model) {
        List<ItemCarrito> carrito = obtenerCarrito(session);

        if (carrito == null || carrito.isEmpty()) {
            return "redirect:/catalogo";
        }

        int totalCompra = 0;
        int itemsComprados = 0;

        for (ItemCarrito item : carrito) {
            if (item != null && item.getProducto() != null) {
                totalCompra += item.getSubtotal();
                itemsComprados += item.getCantidad();

                // Actualizar inventario en la base de datos
                Inventario prod = inventarioRepo.findById(item.getProducto().getIdProducto()).orElse(null);
                if (prod != null) {
                    int nuevoStock = prod.getCantidad() - item.getCantidad();
                    prod.setCantidad(Math.max(nuevoStock, 0));
                    inventarioRepo.save(prod);
                }
            }
        }

        // Pasar datos resumen a la vista de confirmación
        model.addAttribute("metodoPago", metodoPago);
        model.addAttribute("totalCompra", totalCompra);
        model.addAttribute("itemsComprados", itemsComprados);

        // Limpiar el carrito de la sesión
        session.removeAttribute("carrito");

        return "cliente/confirmacion";
    }
}