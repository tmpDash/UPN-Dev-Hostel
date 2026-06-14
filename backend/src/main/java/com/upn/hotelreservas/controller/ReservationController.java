package com.upn.hotelreservas.controller;

import com.upn.hotelreservas.model.Reservation;
import com.upn.hotelreservas.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reservations")
@Tag(name = "Reservas", description = "Operaciones para gestionar las reservas de los clientes")
public class ReservationController {

    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @Operation(summary = "Listar todas las reservas", description = "Retorna una lista de todas las reservas registradas.")
    @GetMapping
    public List<Reservation> listar() {
        return service.listarTodas();
    }

    @GetMapping("/recent")
    public List<Reservation> recientes() {
        return service.listarRecientes();
    }

    @Operation(summary = "Obtener reserva por ID", description = "Busca los detalles de una reserva específica.")
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> obtener(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear nueva reserva", description = "Registra una nueva reserva en el sistema.")
    @PostMapping
    public Reservation crear(@Valid @RequestBody Reservation reservation) {
        return service.guardar(reservation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> actualizar(@PathVariable Long id, @Valid @RequestBody Reservation reservation) {
        return service.buscarPorId(id)
                .map(existing -> {
                    reservation.setId(id);
                    return ResponseEntity.ok(service.guardar(reservation));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
