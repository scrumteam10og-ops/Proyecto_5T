package com.example.demo.service;

import com.example.demo.dto.ItemCarrito;
import com.example.demo.model.DetalleVenta;
import com.example.demo.model.Factura;
import com.example.demo.model.Inventario;
import com.example.demo.model.Usuario;
import com.example.demo.model.Venta;
import com.example.demo.repository.InventarioRepository;
import com.example.demo.repository.FacturaRepository;
import com.example.demo.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final InventarioRepository inventarioRepository;
    private final FacturaRepository facturaRepository;

    public VentaService(VentaRepository ventaRepository, InventarioRepository inventarioRepository,
                        FacturaRepository facturaRepository) {
        this.ventaRepository = ventaRepository;
        this.inventarioRepository = inventarioRepository;
        this.facturaRepository = facturaRepository;
    }

    @Transactional
    public Factura registrarVenta(Usuario usuario, List<ItemCarrito> carrito, String metodoPago) {
        if (carrito == null || carrito.isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío.");
        }

        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setMetodoPago(metodoPago);

        double totalGeneral = 0.0;

        for (ItemCarrito itemCarrito : carrito) {
            Inventario inventarioSession = itemCarrito.getInventario();
            Integer cantidad = itemCarrito.getCantidad();

            // Buscar en la base de datos para obtener el stock y datos actualizados
            Inventario item = inventarioRepository.findById(inventarioSession.getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Ítem no encontrado en la base de datos"));

            if (item.getCantidad() < cantidad) {
                throw new RuntimeException("Stock insuficiente para el producto seleccionado.");
            }

            // Descontar inventario
            item.setCantidad(item.getCantidad() - cantidad);
            inventarioRepository.save(item);

            // Crear el detalle de la venta (se pasa el valor conversor .doubleValue())
            DetalleVenta detalle = new DetalleVenta(item, cantidad, item.getValorVenta().doubleValue());
            venta.agregarDetalle(detalle);

            totalGeneral += detalle.getSubtotal();
        }

        venta.setTotal(totalGeneral);
        Venta ventaGuardada = ventaRepository.save(venta);

        Factura factura = new Factura();
        factura.setVenta(ventaGuardada);
        factura.setTotal((int) Math.round(totalGeneral));
        return facturaRepository.save(factura);
    }

    public List<Venta> listarVentasPorUsuario(Usuario usuario) {
        return ventaRepository.findByUsuario(usuario);
    }
}