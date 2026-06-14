package com.upn.hotelreservas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Future;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "reservations")
@Data
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Column(nullable = false)
    private String customerName;

    @NotNull(message = "La fecha de entrada es obligatoria")
    @FutureOrPresent(message = "La fecha de entrada no puede estar en el pasado")
    @Column(nullable = false)
    private LocalDate checkInDate;

    @NotNull(message = "La fecha de salida es obligatoria")
    @Future(message = "La fecha de salida debe ser en el futuro")
    @Column(nullable = false)
    private LocalDate checkOutDate;

    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private Double totalPrice;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "reservation_id")
    private List<ReservationServiceDetail> addedServices;
}
