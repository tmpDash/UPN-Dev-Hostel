package com.upn.hotelreservas.controller;

import com.upn.hotelreservas.model.Hotel;
import com.upn.hotelreservas.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

/**
 * REST controller exposing CRUD operations for Hotel entities.
 */
@RestController
@RequestMapping("/hoteles")
@Tag(name = "Sedes / Hoteles", description = "Gestión de las diferentes sedes físicas del hotel")
public class HotelController {

    private final HotelService service;

    public HotelController(HotelService service) {
        this.service = service;
    }

    /** List all hotels */
    @Operation(summary = "Listar sedes", description = "Obtiene la lista de todos los hoteles registrados en la cadena.")
    @GetMapping
    public List<Hotel> listar() {
        return service.buscarTodos();
    }

    /** Get a hotel by its id */
    @GetMapping("/{id}")
    public ResponseEntity<Hotel> obtener(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Create a new hotel */
    @PostMapping
    public Hotel crear(@Valid @RequestBody Hotel hotel) {
        return service.crear(hotel);
    }

    /** Update an existing hotel */
    @PutMapping("/{id}")
    public Hotel actualizar(@PathVariable Long id, @Valid @RequestBody Hotel hotel) {
        return service.actualizar(id, hotel);
    }

    /** Delete a hotel by id */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
