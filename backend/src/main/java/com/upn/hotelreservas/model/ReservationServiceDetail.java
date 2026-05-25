package com.upn.hotelreservas.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "reservation_service_details")
@Data
public class ReservationServiceDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long serviceId;
    private String name;
    private Integer quantity;
    private Double price;
}
