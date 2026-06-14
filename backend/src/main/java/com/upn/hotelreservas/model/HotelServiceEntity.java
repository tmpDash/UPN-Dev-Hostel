package com.upn.hotelreservas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Entity
@Table(name = "hotel_services")
@Data
public class HotelServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del servicio es obligatorio")
    private String name;
    private String description;
    
    @NotNull(message = "El precio es obligatorio")
    @PositiveOrZero(message = "El precio no puede ser negativo")
    private Double price;
    private String icon;

    @Enumerated(EnumType.STRING)
    private ServiceCategory category;

    private Boolean isActive;
}
