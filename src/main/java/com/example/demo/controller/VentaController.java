package com.example.demo.controller;

import com.example.demo.dto.ItemCarrito;
import com.example.demo.model.Usuario;
import com.example.demo.model.Factura;
import com.example.demo.repository.FacturaRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.VentaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/venta")
public class VentaController {

    private final VentaService ventaService;
    private final UsuarioRepository usuarioRepository;
    private final FacturaRepository facturaRepository;

    public VentaController(VentaService ventaService, UsuarioRepository usuarioRepository,
                           FacturaRepository facturaRepository) {
        this.ventaService = ventaService;
        this.usuarioRepository = usuarioRepository;
        this.facturaRepository = facturaRepository;
    }

    @PostMapping("/checkout")
    public String realizarCompra(@RequestParam(name = "metodoPago", defaultValue = "EFECTIVO") String metodoPago,
                                 HttpSession session,
                                 Authentication authentication,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para realizar la compra.");
            return "redirect:/login";
        }

        // Leer como List<ItemCarrito>
        @SuppressWarnings("unchecked")
        List<ItemCarrito> carrito = (List<ItemCarrito>) session.getAttribute("carrito");

        if (carrito == null || carrito.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El carrito se encuentra vacío.");
            return "redirect:/catalogo";
        }

        try {
            String correo = authentication.getName();
            Usuario usuario = usuarioRepository.findByCorreo(correo)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el correo: " + correo));

                Factura factura = ventaService.registrarVenta(usuario, carrito, metodoPago);

            session.removeAttribute("carrito");
                model.addAttribute("facturaId", factura.getIdFactura());
                model.addAttribute("metodoPago", metodoPago);
                model.addAttribute("totalCompra", factura.getTotal());
                model.addAttribute("itemsComprados", carrito.stream()
                    .mapToInt(ItemCarrito::getCantidad)
                    .sum());
                return "cliente/confirmacion";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar la compra: " + e.getMessage());
            return "redirect:/catalogo";
        }
    }

        @GetMapping("/factura/{id}/descargar")
        public ResponseEntity<byte[]> descargarFactura(@PathVariable Integer id,
                               Authentication authentication) {
        Factura factura = facturaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada"));

        if (authentication == null || !authentication.isAuthenticated()
            || !factura.getVenta().getUsuario().getCorreo().equals(authentication.getName())) {
            return ResponseEntity.status(403).build();
        }

        StringBuilder contenido = new StringBuilder();
        contenido.append("STARDINASTY - FACTURA\n");
        contenido.append("Factura: ").append(factura.getIdFactura()).append("\n");
        contenido.append("Cliente: ").append(factura.getVenta().getUsuario().getNombre()).append("\n");
        contenido.append("Fecha: ").append(factura.getFecha()).append("\n");
        contenido.append("Metodo de pago: ").append(factura.getVenta().getMetodoPago()).append("\n\n");
        contenido.append("Producto | Cantidad | Precio unitario | Subtotal\n");
        factura.getVenta().getDetalles().forEach(detalle -> contenido
            .append(detalle.getInventario().getNombre()).append(" | ")
            .append(detalle.getCantidad()).append(" | $")
            .append(detalle.getPrecioUnitario()).append(" | $")
            .append(detalle.getSubtotal()).append("\n"));
        contenido.append("\nTOTAL: $").append(factura.getTotal()).append("\n");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDisposition(ContentDisposition.attachment()
            .filename("factura-" + factura.getIdFactura() + ".txt")
            .build());
        return ResponseEntity.ok().headers(headers)
            .body(contenido.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }
}