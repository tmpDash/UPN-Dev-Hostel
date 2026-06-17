# Evidencia de Práctica de Campo: Calidad y Pruebas de Desarrollo de Software

**Fecha de Inicio:** 13 de Junio de 2026
**Fecha de Actualización:** 15 de Junio de 2026
**Proyecto:** Hotel-Luxury (White-label)
**Conversation ID (Antigravity):** dc686ec4-3b05-4b43-997b-f08120958542
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
* [x] **Fase 3 (v1.2.0):** Resolver vulnerabilidades detectadas y optimizar frontend (Seguridad ZAP y Lighthouse resueltos con éxito).
* [x] **Fase 4 (v1.3.0):** Refactorización comercial (White-label). Eliminación de dependencias académicas, reestructuración profunda de paquetes Java y actualización de branding.
* [x] **Fase 5 (v1.4.0):** Rediseño UI/UX (TailwindCSS), Internacionalización (I18n), y Auditoría Final (OWASP/Lighthouse/JUnit).

---

## 5. Implementación de Optimizaciones (v1.2.0 y v1.3.0)

1. **Resolución de Riesgos ZAP (v1.2.0)**:
   - Se erradicó la fuga de hashes BCrypt (Riesgo Alto) inyectando @JsonIgnore en las entidades correspondientes.
   - Se fortificó Spring Security con X-Frame-Options: DENY y Content Security Policy (CSP).
2. **Mejoras de Lighthouse (v1.2.0)**:
   - Se implementaron métricas de SEO y Accesibilidad (Atributos ARIA, meta descripciones, tamaños explícitos). El SEO del login alcanzó el 100%.
3. **Refactorización White-label (v1.3.0)**:
   - Migración masiva de la estructura de paquetes Java (com.upn.hotelreservas a com.luxury.hotel).
   - Saneamiento de base de datos y scripts (import.sql) para reflejar correos corporativos @luxuryhotel.com.
   - Limpieza de vistas HTML en Angular para remover el contexto universitario.
   - Todas las 69 pruebas unitarias se verificaron exitosamente bajo la nueva estructura.
4. **Rediseño UI/UX y Auditoría Final (v1.4.0)**:
   - **Frontend UI/UX:** Migración de estilos a TailwindCSS y preline. Traducción integral del sistema al español.
   - **Performance y Accesibilidad:** Lighthouse reportó 92% Accesibilidad, 100% SEO, 96% Mejores Prácticas (Mejora significativa frente a v1.2.0).
   - **Seguridad Backend (OWASP):** Implementación sólida validada de JWT Stateless, CORS restringido a frontend, y desactivación de CSRF por diseño API.
   - **Cobertura de Pruebas (JUnit/JaCoCo):** Las 69 pruebas unitarias se mantuvieron estables (100% PASS), alcanzando **98% de cobertura en Controllers** y **92% en Services**.
