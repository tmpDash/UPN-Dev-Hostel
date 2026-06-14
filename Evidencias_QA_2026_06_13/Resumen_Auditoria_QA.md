# Evidencia de Práctica de Campo: Calidad y Pruebas de Desarrollo de Software

**Fecha:** 13 de Junio de 2026
**Proyecto:** Hotel-Reservas-UPN-2
**Fase Actual:** Pruebas Unitarias Backend y Cobertura (Finalizado) / Pruebas Web (Pendiente)

**ID de Conversación AI:** `c398f06c-3ec3-4ccc-9671-8aac371dc401`
**Ruta de Artefactos y Logs AI:** `C:\Users\epion\.gemini\antigravity-ide\brain\c398f06c-3ec3-4ccc-9671-8aac371dc401`

---

## 1. Historial de Actividades Realizadas (Backend)

Durante esta fase, se llevó a cabo la inspección y refactorización del código backend en Java (Spring Boot) con el fin de fortalecer la integridad de los datos y asegurar la calidad a través de pruebas automatizadas:

1. **Configuración de Dependencias**: 
   - Se integró `spring-boot-starter-validation` en el archivo `pom.xml` para habilitar el marco de validaciones JSR-303.
2. **Implementación de Validaciones (Modelos y DTOs)**:
   - Se aplicaron anotaciones de restricción (`@NotBlank`, `@NotNull`, `@Email`, `@Future`, `@PositiveOrZero`) en las entidades `Hotel`, `HotelServiceEntity`, `Reservation`, `StaffUser` y el DTO `LoginRequest`.
3. **Refactorización de Controladores**:
   - Se protegió la entrada de datos agregando la anotación `@Valid` en los métodos de creación (POST) y actualización (PUT) de todos los controladores.
4. **Desarrollo de Casos de Prueba (Testing)**:
   - Se crearon y ajustaron clases de prueba usando `MockMvc` para todos los controladores (`AuthController`, `ReservationController`, `HotelController`, `HotelServiceController`, `StaffUserController`, `ReportController`).
   - Se evaluaron tanto los *Happy Paths* (flujos exitosos) como los *Unhappy Paths* (errores por datos inválidos, contraseñas incorrectas, y restricciones violadas).
5. **Corrección de Bugs (Refactorización)**:
   - Se solucionó un error crítico de deserialización de Jackson en el modelo `StaffUser` agregando `@JsonIgnore` al método `getAuthorities()`.

---

## 2. Artefactos y Evidencias Generadas

- **Suite de Pruebas Unitarias**: Un total de **47 casos de prueba automatizados** ejecutándose sin errores.
- **Reporte de Cobertura JaCoCo**: 
  - Se generó un reporte en HTML que certifica una **cobertura del 98% de instrucciones** y un **100% de cobertura de ramificaciones (branches)** en el paquete de Controladores (`com.upn.hotelreservas.controller`), validando exhaustivamente la capa API REST.
  - *Ruta del reporte:* `backend/target/site/jacoco/index.html`

---

## 3. Siguientes Pasos y Tareas Pendientes (Frontend y Seguridad)

Para cumplir con el 100% de los requerimientos del curso de Calidad y Pruebas de Software, el siguiente paso es auditar el entorno **Frontend (Angular)** y su conexión de seguridad:

* [ ] **Levantar Entorno Local Web**: Iniciar el servidor local del proyecto Angular (`http://localhost:4200`).
* [ ] **Análisis con Google Lighthouse**:
  * Ejecutar reporte de *Performance* (Rendimiento).
  * Ejecutar reporte de *Accessibility* (Accesibilidad).
  * Ejecutar reporte de *Best Practices* (Mejores Prácticas).
  * Ejecutar reporte de *SEO*.
* [ ] **Escaneo de Vulnerabilidades con OWASP ZAP**:
  * Configurar ZAP para interceptar y escanear pasiva y activamente el tráfico en el dominio local.
  * Evaluar cabeceras de seguridad, protección contra ataques CSRF, XSS y gestión de Cookies.
* [ ] **Creación de Entregables Finales**:
  * Redactar la **Lista de Control de Calidad (Checklist)** final abarcando ambos entornos (Backend y Frontend) basándonos en la rúbrica del curso.
