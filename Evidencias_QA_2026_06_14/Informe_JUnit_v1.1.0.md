# Informe de Pruebas Unitarias (Backend) - Capa de Servicios y Lógica de Negocio
**Versión evaluada:** v1.1.0
**Fecha:** 14 de Junio de 2026
**Herramientas:** JUnit 5, Mockito, Spring Boot Test, JaCoCo

Este documento detalla la ejecución de la segunda fase de pruebas unitarias, realizada sobre la versión 1.1.0, enfocada en la Capa de Servicios y la validación estricta de las reglas de negocio, aislando la conexión a la base de datos mediante Mockito.

---

## 1. Resumen de Ejecución
- **Total de pruebas acumuladas:** 69 (47 de Controladores + 22 de Servicios)
- **Pruebas de Servicios exitosas:** 22
- **Pruebas fallidas:** 0
- **Cobertura de Código (JaCoCo):** Se generó un nuevo reporte local `jacoco_v1.1.0.zip` que certifica la cobertura de las nuevas reglas de negocio implementadas en los servicios.

## 2. Clases Evaluadas (`@InjectMocks` y `@Mock`)
1. `ReservationServiceTest`
2. `HotelServiceTest`
3. `StaffUserServiceTest`
4. `ReportServiceTest`

## 3. Lógica de Negocio Probada (Reglas Clave)
- **Reservas (ReservationService):** 
  - Se probó la validación estricta de fechas, asegurando que el sistema lance un `IllegalArgumentException` si la fecha de *Check-out* es anterior o igual a la de *Check-in*.
- **Usuarios (StaffUserService):** 
  - Se validó la regla de negocio de *Correos Únicos*, comprobando que el sistema lance una excepción si se intenta guardar un usuario con un email que ya existe en la base de datos (simulada por Mockito).
- **Reportes (ReportService):**
  - Se simuló el cálculo de estadísticas (Ingresos mensuales, Ocupación), verificando el descarte correcto de las reservas canceladas.
- **Aislamiento de Base de Datos:**
  - Todas las operaciones CRUD (`save`, `findById`, `delete`) fueron interceptadas exitosamente usando `when(...).thenReturn(...)` y `verify(...)` de Mockito, logrando pruebas ultra rápidas sin tocar una base de datos real.

## Conclusión
La arquitectura del proyecto está correctamente estructurada por capas y las validaciones de negocio están sólidamente cubiertas por pruebas unitarias automatizadas. La evidencia técnica del código ha quedado respaldada en GitHub bajo el tag `v1.1.0`.
