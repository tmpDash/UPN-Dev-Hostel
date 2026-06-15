# Evidencia de Práctica de Campo: Calidad y Pruebas de Desarrollo de Software

**Fecha de Inicio:** 13 de Junio de 2026
**Fecha de Actualización:** 14 de Junio de 2026
**Proyecto:** Hotel-Reservas-UPN-2
**Fase Actual:** Auditoría Integral QA Completada (v1.1.0)

---

## 1. Historial de Actividades Realizadas (Backend - v1.0.0 y v1.1.0)

Durante esta fase, se llevó a cabo la inspección, refactorización y adición de lógica de negocio en el backend en Java (Spring Boot):

1. **Configuración de Dependencias**: 
   - Se integró `spring-boot-starter-validation` en el archivo `pom.xml` para habilitar el marco de validaciones JSR-303.
2. **Implementación de Validaciones (Modelos y DTOs)**:
   - Se aplicaron anotaciones de restricción (`@NotBlank`, `@NotNull`, `@Email`, `@Future`, `@PositiveOrZero`).
3. **Refactorización de Controladores y Servicios**:
   - Se protegió la entrada de datos agregando la anotación `@Valid` en los métodos de Controladores.
   - Se inyectó lógica de negocio en los Servicios (ej. validación de emails duplicados y fechas lógicas de reservas).
4. **Desarrollo de Casos de Prueba (Testing - JUnit 5 & Mockito)**:
   - **Fase 1:** Se evaluó la capa de Controladores (47 pruebas).
   - **Fase 2:** Se evaluó la capa de Servicios aislando la base de datos (22 nuevas pruebas).
5. **Corrección de Bugs**:
   - Solucionado error crítico de deserialización de Jackson (`@JsonIgnore` en `getAuthorities()`).

---

## 2. Auditoría Frontend y Seguridad (v1.1.0)

Se realizaron pruebas exhaustivas de la aplicación cliente (Angular) y de la interacción HTTP con el backend:

1. **Análisis de Interfaz con Google Lighthouse**:
   - **Login:** Mejores Prácticas (100), Accesibilidad (97), SEO (91), Rendimiento (68).
   - **Dashboard:** Mejores Prácticas (100), Accesibilidad (79), SEO (73), Rendimiento (66).
   - *Plan de acción (v1.2.0):* Optimizar imágenes a formato WebP y agregar atributos ARIA faltantes.
2. **Escaneo de Vulnerabilidades con OWASP ZAP (DAST)**:
   - **Riesgo Alto:** 1 hallazgo (Divulgación de Hash BCrypt en respuestas de API).
   - **Riesgo Medio:** 4 hallazgos (Ausencia de CSP, CORS permisivo, falta de Anti-Clickjacking y SRI).
   - *Plan de acción (v1.2.0):* Aplicar filtros con `@JsonIgnore` y configurar cabeceras en Spring Security.

---

## 3. Artefactos y Evidencias Generadas

- **Suite de Pruebas Unitarias (Backend)**: Un total de **69 casos de prueba automatizados** ejecutándose con éxito (`BUILD SUCCESS`).
- **Reportes de Cobertura JaCoCo**: 
  - Generados localmente para v1.0.0 (Controladores) y v1.1.0 (Servicios).
- **Reportes QA (Frontend y Seguridad)**:
  - Exportados en formato PDF y HTML desde Lighthouse y ZAP.
  - Informes técnicos consolidados en la carpeta de evidencias correspondiente.

---

## 4. Estado de Tareas (Checklist)

* [x] **Pruebas Unitarias Backend:** Controladores probados.
* [x] **Pruebas Unitarias Backend:** Servicios probados con lógica de negocio.
* [x] **Análisis con Google Lighthouse**: Rendimiento, Accesibilidad, Mejores Prácticas y SEO auditados.
* [x] **Escaneo de Vulnerabilidades con OWASP ZAP**: Escaneo activo (Active Scan) finalizado.
* [x] **Documentación**: README.md e informes de evidencias actualizados.
* [ ] **Fase 3 (Siguiente paso):** Resolver vulnerabilidades detectadas y optimizar frontend (v1.2.0).
