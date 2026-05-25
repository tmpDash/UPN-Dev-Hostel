package com.upn.hotelreservas.controller;

import com.upn.hotelreservas.dto.ReportStatsDTO;
import com.upn.hotelreservas.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
@Tag(name = "Reportes", description = "Endpoints para obtener estadísticas y métricas del sistema")
public class ReportController {

    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    @Operation(summary = "Estadísticas Generales", description = "Retorna un resumen de métricas clave (total reservas, ocupación, etc.)")
    @GetMapping("/stats")
    public ReportStatsDTO getStats() {
        return service.getGeneralStats();
    }
}
