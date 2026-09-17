package com.example.demo.service;

import com.example.demo.model.DetalleVenta;
import com.example.demo.model.Inventario;
import com.example.demo.model.Venta;
import com.example.demo.repository.InventarioRepository;
import com.example.demo.repository.VentaRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class ReportePdfService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final InventarioRepository inventarioRepository;
    private final VentaRepository ventaRepository;

    public ReportePdfService(InventarioRepository inventarioRepository, VentaRepository ventaRepository) {
        this.inventarioRepository = inventarioRepository;
        this.ventaRepository = ventaRepository;
    }

    public byte[] inventario() {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, output);
        document.open();
        addTitle(document, "REPORTE DE INVENTARIO");

        Table table = new Table(6);
        table.setWidth(100);
        addHeader(table, "ID", "Referencia", "Producto", "Categoría", "Stock", "Precio");
        for (Inventario item : inventarioRepository.findAll()) {
            table.addCell(String.valueOf(item.getIdProducto()));
            table.addCell(value(item.getReferenciaProducto()));
            table.addCell(value(item.getNombre()));
            table.addCell(item.getCategoria() == null ? "Sin categoría" : value(item.getCategoria().getNombre()));
            table.addCell(String.valueOf(item.getCantidad()));
            table.addCell("$" + item.getValorVenta());
        }
        document.add(table);
        document.close();
        return output.toByteArray();
    }

    public byte[] ventas() {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, output);
        document.open();
        addTitle(document, "REPORTE DE VENTAS");

        Table table = new Table(7);
        table.setWidth(100);
        addHeader(table, "ID Venta", "Fecha", "Cliente", "Método", "Estado", "Productos", "Total");
        for (Venta venta : ventaRepository.findAll()) {
            table.addCell(String.valueOf(venta.getIdVenta()));
            table.addCell(venta.getFecha() == null ? "" : venta.getFecha().format(DATE_FORMAT));
            table.addCell(venta.getUsuario() == null ? "" : value(venta.getUsuario().getNombre()));
            table.addCell(value(venta.getMetodoPago()));
            table.addCell(value(venta.getEstado()));
            table.addCell(String.valueOf(venta.getDetalles().stream().mapToInt(DetalleVenta::getCantidad).sum()));
            table.addCell("$" + venta.getTotal());
        }
        document.add(table);
        document.close();
        return output.toByteArray();
    }

    private void addTitle(Document document, String title) {
        Font font = new Font(Font.HELVETICA, 18, Font.BOLD, new Color(20, 20, 20));
        Paragraph paragraph = new Paragraph(title, font);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingAfter(18);
        document.add(paragraph);
    }

    private void addHeader(Table table, String... headers) {
        Font font = new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE);
        for (String header : headers) {
            com.lowagie.text.Cell cell = new com.lowagie.text.Cell(new Phrase(header, font));
            cell.setBackgroundColor(new Color(25, 25, 25));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.BOX);
            table.addCell(cell);
        }
    }

    private String value(String value) {
        return value == null ? "" : value;
    }
}
