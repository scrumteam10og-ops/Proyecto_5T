package com.example.demo.controller;

import com.example.demo.model.Categoria;
import com.example.demo.model.CompraInicial;
import com.example.demo.model.Inventario;
import com.example.demo.model.Usuario;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.CompraInicialRepository;
import com.example.demo.repository.InventarioRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.repository.VentaRepository;
import com.example.demo.service.ReportePdfService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final InventarioRepository inventarioRepo;
    private final CompraInicialRepository compraRepo;
    private final CategoriaRepository categoriaRepo;
    private final VentaRepository ventaRepo;
    private final UsuarioRepository usuarioRepo;
    private final ReportePdfService reportePdfService;

    public AdminController(InventarioRepository inventarioRepo,
                           CompraInicialRepository compraRepo,
                           CategoriaRepository categoriaRepo,
                           VentaRepository ventaRepo,
                           UsuarioRepository usuarioRepo,
                           ReportePdfService reportePdfService) {
        this.inventarioRepo = inventarioRepo;
        this.compraRepo = compraRepo;
        this.categoriaRepo = categoriaRepo;
        this.ventaRepo = ventaRepo;
        this.usuarioRepo = usuarioRepo;
        this.reportePdfService = reportePdfService;
    }

    // ==========================================
    // 1. DASHBOARD PRINCIPAL
    // ==========================================
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("cantInventario", inventarioRepo.count());
        model.addAttribute("cantCompras", compraRepo.count());
        model.addAttribute("cantCategorias", categoriaRepo.count());
        model.addAttribute("cantVentas", ventaRepo.count());
        model.addAttribute("cantUsuarios", usuarioRepo.count());
        return "admin/dashboard";
    }

    // ==========================================
    // 2. MÓDULO INVENTARIO
    // ==========================================
    @GetMapping("/inventario")
    public String verInventario(Model model) {
        model.addAttribute("listaInventario", inventarioRepo.findAll());
        return "admin/inventario-list";
    }

    @GetMapping("/reportes/inventario.pdf")
    public ResponseEntity<byte[]> reporteInventario() {
        return pdfResponse(reportePdfService.inventario(), "reporte-inventario.pdf");
    }

    @GetMapping("/reportes/ventas.pdf")
    public ResponseEntity<byte[]> reporteVentas() {
        return pdfResponse(reportePdfService.ventas(), "reporte-ventas.pdf");
    }

    private ResponseEntity<byte[]> pdfResponse(byte[] content, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
        return ResponseEntity.ok().headers(headers).body(content);
    }

    @GetMapping("/inventario/nuevo")
    public String nuevoProductoForm(Model model) {
        model.addAttribute("producto", new Inventario());
        model.addAttribute("categorias", categoriaRepo.findAll());
        return "admin/inventario-form";
    }

    @PostMapping("/inventario/guardar")
    public String guardarProducto(@ModelAttribute("producto") Inventario producto) {
        // Estado por defecto: 'A' (Activo)
        if (producto.getEstado() == null || producto.getEstado().isBlank()) {
            producto.setEstado("A");
        }

        // Cálculo exacto del valor total
        if (producto.getCantidad() != null && producto.getValorVenta() != null) {
            producto.setValorTotal(producto.getCantidad() * producto.getValorVenta());
        }

        inventarioRepo.save(producto);
        return "redirect:/admin/inventario";
    }

    @GetMapping("/inventario/editar/{id}")
    public String editarProductoForm(@PathVariable("id") Integer id, Model model) {
        Inventario producto = inventarioRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de producto no válido: " + id));
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaRepo.findAll());
        return "admin/inventario-form";
    }

    @GetMapping("/inventario/eliminar/{id}")
    public String eliminarProducto(@PathVariable("id") Integer id) {
        inventarioRepo.deleteById(id);
        return "redirect:/admin/inventario";
    }

    // ==========================================
    // 3. MÓDULO STOCK INICIAL (CON UTILIDAD 60%)
    // ==========================================
    @GetMapping("/stock")
    public String verStock(Model model) {
        model.addAttribute("listaCompras", compraRepo.findAll());
        return "admin/stock-list";
    }

    @GetMapping("/stock/nuevo")
    public String nuevoStockForm(Model model) {
        model.addAttribute("compra", new CompraInicial());
        model.addAttribute("categorias", categoriaRepo.findAll());
        return "admin/stock-form";
    }

    @PostMapping("/stock/guardar")
    public String guardarStock(@ModelAttribute("compra") CompraInicial compra,
                               @RequestParam(name = "idCategoria", required = false) Integer idCategoria) {
        
        if (compra.getCantidad() != null && compra.getValorUnitario() != null) {
            // 1. Calcular valor total del costo en la compra inicial
            compra.setValorTotal(compra.getCantidad() * compra.getValorUnitario());
        }
        
        // Guardar la compra inicial en BD
        compraRepo.save(compra);

        // 2. Calcular Precio de Venta con Margen del 60% de Utilidad
        if (compra.getValorUnitario() != null) {
            int valorVentaConUtilidad = (int) Math.round(compra.getValorUnitario() * 1.60);

            // Buscar si la referencia ya existe en el inventario
            Inventario productoInventario = inventarioRepo.findAll().stream()
                    .filter(p -> p.getReferenciaProducto() != null && p.getReferenciaProducto().equalsIgnoreCase(compra.getReferenciaProducto()))
                    .findFirst()
                    .orElse(null);

            if (productoInventario == null) {
                // Registrar nuevo producto en inventario
                productoInventario = new Inventario();
                productoInventario.setReferenciaProducto(compra.getReferenciaProducto());
                productoInventario.setNombre(compra.getNombre());
                productoInventario.setDescripcion(compra.getDescripcion());
                productoInventario.setCantidad(compra.getCantidad());
                productoInventario.setValorVenta(valorVentaConUtilidad);
                productoInventario.setValorTotal(compra.getCantidad() * valorVentaConUtilidad);
                productoInventario.setEstado("A");

                // Asignar categoría seleccionada o primera disponible
                if (idCategoria != null) {
                    categoriaRepo.findById(idCategoria).ifPresent(productoInventario::setCategoria);
                } else if (!categoriaRepo.findAll().isEmpty()) {
                    productoInventario.setCategoria(categoriaRepo.findAll().get(0));
                }
            } else {
                // Si el producto existe, sumar stock y actualizar valor de venta con la nueva utilidad
                int nuevaCantidad = productoInventario.getCantidad() + compra.getCantidad();
                productoInventario.setCantidad(nuevaCantidad);
                productoInventario.setValorVenta(valorVentaConUtilidad);
                productoInventario.setValorTotal(nuevaCantidad * valorVentaConUtilidad);
            }

            inventarioRepo.save(productoInventario);
        }

        return "redirect:/admin/stock";
    }

    @GetMapping("/stock/editar/{id}")
    public String editarStockForm(@PathVariable("id") Integer id, Model model) {
        CompraInicial compra = compraRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de stock no válido: " + id));
        model.addAttribute("compra", compra);
        model.addAttribute("categorias", categoriaRepo.findAll());
        return "admin/stock-form";
    }

    @GetMapping("/stock/eliminar/{id}")
    public String eliminarStock(@PathVariable("id") Integer id) {
        compraRepo.deleteById(id);
        return "redirect:/admin/stock";
    }

    // ==========================================
    // 4. MÓDULO CATEGORÍAS
    // ==========================================
    @GetMapping("/categorias")
    public String verCategorias(Model model) {
        model.addAttribute("listaCategorias", categoriaRepo.findAll());
        return "admin/categorias-list";
    }

    @GetMapping("/categorias/nuevo")
    public String nuevaCategoriaForm(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "admin/categoria-form";
    }

    @PostMapping("/categorias/guardar")
    public String guardarCategoria(@ModelAttribute("categoria") Categoria categoria) {
        categoriaRepo.save(categoria);
        return "redirect:/admin/categorias";
    }

    @GetMapping("/categorias/editar/{id}")
    public String editarCategoriaForm(@PathVariable("id") Integer id, Model model) {
        Categoria categoria = categoriaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de categoría no válido: " + id));
        model.addAttribute("categoria", categoria);
        return "admin/categoria-form";
    }

    @GetMapping("/categorias/eliminar/{id}")
    public String eliminarCategoria(@PathVariable("id") Integer id) {
        categoriaRepo.deleteById(id);
        return "redirect:/admin/categorias";
    }

    // ==========================================
    // 5. MÓDULO USUARIOS
    // ==========================================
    @GetMapping("/usuarios")
    public String verUsuarios(Model model) {
        model.addAttribute("listaUsuarios", usuarioRepo.findAll());
        return "admin/usuarios-list";
    }

    @GetMapping("/usuarios/nuevo")
    public String nuevoUsuarioForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "admin/usuarios-form";
    }

    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(@ModelAttribute("usuario") Usuario usuario) {
        if (usuario.getIdUsuario() == null) {
            if (usuario.getEstado() == null || usuario.getEstado().isBlank()) {
                usuario.setEstado("A");
            }
        } else {
            Usuario actual = usuarioRepo.findById(usuario.getIdUsuario())
                    .orElseThrow(() -> new IllegalArgumentException("ID de usuario no válido: " + usuario.getIdUsuario()));
            if (usuario.getContrasena() == null || usuario.getContrasena().isBlank()) {
                usuario.setContrasena(actual.getContrasena());
            }
        }
        usuarioRepo.save(usuario);
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/usuarios/editar/{id}")
    public String editarUsuarioForm(@PathVariable("id") Integer id, Model model) {
        Usuario usuario = usuarioRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de usuario no válido: " + id));
        model.addAttribute("usuario", usuario);
        return "admin/usuarios-form";
    }

    @GetMapping("/usuarios/cambiar-estado/{id}")
    public String cambiarEstadoUsuario(@PathVariable("id") Integer id) {
        usuarioRepo.findById(id).ifPresent(u -> {
            u.setEstado("A".equalsIgnoreCase(u.getEstado()) ? "I" : "A");
            usuarioRepo.save(u);
        });
        return "redirect:/admin/usuarios";
    }
}