package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "primer_nom", nullable = false)
    private String primerNom;

    @Column(name = "segundo_nom")
    private String segundoNom;

    @Column(name = "primer_apelli", nullable = false)
    private String primerApelli;

    @Column(name = "segundo_apelli")
    private String segundoApelli;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "correo", unique = true, nullable = false)
    private String correo;

    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    @Column(name = "rol")
    private String rol;

    @Column(name = "estado")
    private String estado;

    public Usuario() {}

    // Getters y Setters
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getPrimerNom() { return primerNom; }
    public void setPrimerNom(String primerNom) { this.primerNom = primerNom; }

    public String getSegundoNom() { return segundoNom; }
    public void setSegundoNom(String segundoNom) { this.segundoNom = segundoNom; }

    public String getPrimerApelli() { return primerApelli; }
    public void setPrimerApelli(String primerApelli) { this.primerApelli = primerApelli; }

    public String getSegundoApelli() { return segundoApelli; }
    public void setSegundoApelli(String segundoApelli) { this.segundoApelli = segundoApelli; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}