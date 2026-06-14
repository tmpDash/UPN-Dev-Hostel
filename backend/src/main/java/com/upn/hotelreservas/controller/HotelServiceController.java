package com.upn.hotelreservas.controller;

import com.upn.hotelreservas.model.HotelServiceEntity;
import com.upn.hotelreservas.service.HotelServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/servicios")
@Tag(name = "Servicios del Hotel", description = "Gestión de servicios adicionales (comida, transporte, limpieza, etc.)")
public class HotelServiceController {

    private final HotelServiceService service;

    public HotelServiceController(HotelServiceService service) {
        this.service = service;
    }

    @Operation(summary = "Listar servicios", description = "Retorna el catálogo de servicios ofrecidos por el hotel.")
    @GetMapping
    public List<HotelServiceEntity> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelServiceEntity> obtener(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public HotelServiceEntity crear(@Valid @RequestBody HotelServiceEntity hotelService) {
        return service.guardar(hotelService);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HotelServiceEntity> actualizar(@PathVariable Long id, @Valid @RequestBody HotelServiceEntity hotelService) {
        return service.buscarPorId(id)
                .map(existing -> {
                    hotelService.setId(id);
                    return ResponseEntity.ok(service.guardar(hotelService));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
