package com.upn.hotelreservas.controller;

import com.upn.hotelreservas.model.StaffUser;
import com.upn.hotelreservas.service.StaffUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "Usuarios / Staff", description = "Gestión de los empleados y perfiles de acceso")
public class StaffUserController {

    private final StaffUserService service;

    public StaffUserController(StaffUserService service) {
        this.service = service;
    }

    @Operation(summary = "Listar empleados", description = "Obtiene la lista de todo el personal registrado.")
    @GetMapping
    public List<StaffUser> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffUser> obtener(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public StaffUser crear(@RequestBody StaffUser user) {
        return service.guardar(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StaffUser> actualizar(@PathVariable Long id, @RequestBody StaffUser user) {
        return service.buscarPorId(id)
                .map(existing -> {
                    user.setId(id);
                    return ResponseEntity.ok(service.guardar(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
