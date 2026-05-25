package com.upn.hotelreservas.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "hotel_services")
@Data
public class HotelServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Double price;
    private String icon;

    @Enumerated(EnumType.STRING)
    private ServiceCategory category;

    private Boolean isActive;
}
