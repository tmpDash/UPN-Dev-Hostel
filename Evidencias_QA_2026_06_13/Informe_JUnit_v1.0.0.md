# Informe de Pruebas Unitarias (Backend) - Capa de Controladores
**Versión evaluada:** v1.0.0
**Fecha:** 13 de Junio de 2026
**Herramientas:** JUnit 5, Mockito, Spring Boot Test, JaCoCo

Este documento detalla la ejecución de pruebas unitarias realizadas en la versión 1.0.0, enfocadas exclusivamente en la Capa de Controladores (API REST).

---

## 1. Resumen de Ejecución
- **Total de pruebas ejecutadas:** 47
- **Pruebas exitosas:** 47
- **Pruebas fallidas:** 0
- **Cobertura de Código (JaCoCo):** 
  - 98% de cobertura de líneas en el paquete `com.upn.hotelreservas.controller`.
  - 100% de cobertura de ramas (branches).

## 2. Clases Evaluadas (`@WebMvcTest`)
1. `AuthControllerTest`
2. `HotelControllerTest`
3. `HotelServiceControllerTest`
4. `ReservationControllerTest`
5. `ReportControllerTest`
6. `StaffUserControllerTest`

## 3. Escenarios Probados (Happy Paths y Unhappy Paths)
- **Validación de Entradas (`@Valid`):** Se comprobó que el sistema rechaza peticiones con correos inválidos, campos vacíos (`@NotBlank`), valores negativos y fechas incoherentes (`@Future`).
- **Lógica de Autenticación:** Verificación de respuestas correctas con credenciales válidas y rechazo ante credenciales erróneas.
- **Gestión de Errores REST:** Confirmación de que los endpoints devuelven códigos HTTP adecuados (`400 Bad Request`, `401 Unauthorized`, `404 Not Found`, `200 OK`, `201 Created`).

## Conclusión
La base de los controladores es estable y las validaciones de entrada JSR-303 funcionan correctamente. El reporte HTML de JaCoCo en formato `.zip` acompaña esta evidencia.
