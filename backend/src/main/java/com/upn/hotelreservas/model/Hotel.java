package com.upn.hotelreservas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Entidad que representa un Hotel en la base de datos.
 */
@Entity
@Table(name = "hoteles")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;
    
    @NotBlank(message = "La ciudad es obligatoria")
    private String ciudad;
    
    @NotNull(message = "El número de habitaciones es obligatorio")
    private Integer habitaciones;
    
    private LocalDate fechaCreacion;

    // ---------- Getters & Setters ----------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public Integer getHabitaciones() { return habitaciones; }
    public void setHabitaciones(Integer habitaciones) { this.habitaciones = habitaciones; }

    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDate fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
