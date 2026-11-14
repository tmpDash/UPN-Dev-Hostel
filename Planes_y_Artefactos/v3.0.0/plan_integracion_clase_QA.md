# Plan de Integración de Calidad y Pruebas — Hotel Reservas UPN v3.0.0
Este documento define la estrategia técnica para alinear el proyecto **Hotel Reservas v3.0.0** con las directrices de calidad, seguridad y fiabilidad enseñadas en las sesiones de clase, y detalla la asignación de responsabilidades del equipo de desarrollo.

---

## 1. Análisis por Herramienta y Estándar (Base Teórica del Curso)

### A. JUnit (Pruebas Unitarias y de Integración)
*Basado en "The Quality Blueprint" y "Agile Testing Ecosystem"*
- **Objetivos de Cobertura:** Elevar la cobertura de instrucciones global a $\geq 60\%$ y la de ramas condicionales a $\geq 40\%$.
- **Mitigación de "Test Smells"**:
  - Evitar el anti-patrón de "Solo el Camino Feliz" (Happy Path). Añadir pruebas para excepciones (`BadCredentialsException`, `UsernameNotFoundException`), valores límites (fechas de checkout anteriores al checkin) y nulos.
  - Asegurar la legibilidad siguiendo la estructura **AAA** (Arrange-Act-Assert) o BDD (Given-When-Then).
- **Alcance de Pruebas:**
  - **Modelos:** Pruebas de entidades JPA (`Reservation`, `StaffUser`).
  - **Servicios:** Validar lógica de negocio, flujos condicionales de roles (ADMIN vs STAFF) y generación de reportes (`ReportService`).
  - **Controladores:** MockMvc para validar códigos de respuesta HTTP (200, 400, 401, 404).

### B. Google Lighthouse (Calidad Web y Accesibilidad)
*Basado en "Software Quality Dimensions" y "The Quality Blueprint"*
- **Puntuaciones Objetivo:** Accesibilidad $\geq 90$, Best Practices $= 100$, SEO $\geq 90$.
- **Rendimiento:**
  - Optimizar la carga del LCP comprimiendo el logo y las imágenes de fondo de JPG/PNG a formatos de última generación (**WebP** o **AVIF**).
  - Definir explícitamente `width` y `height` en etiquetas `<img>` para reducir el Layout Shift (CLS).
- **Buenas Prácticas:**
  - Asegurar que los recursos de CDNs externos cuenten con cabeceras correctas de CORS y verificación de integridad.

### C. OWASP ZAP (Seguridad Dinámica - DAST)
*Basado en "OWASP Web Security Automation" y "Open Source DevSecOps Arsenal"*
- **Enfoque Híbrido (SAST + DAST):** La seguridad absoluta no existe; se requiere análisis estático temprano (SAST) y verificación dinámica (DAST).
- **Remediaciones Aplicadas:**
  - Cabeceras HTTP: CSP restrictiva, `X-Frame-Options: DENY`, `X-Content-Type-Options: nosniff`, e HSTS preactivado en la capa HTTP.
  - Gestión de Cookies: Inyección de flags `HttpOnly`, `Secure` y `SameSite=Strict`.
  - Ocultamiento de la huella tecnológica: Supresión de los headers `Server` y `X-Powered-By`.

### D. Apache JMeter (Pruebas de Carga y Concurrencia)
*Basado en "The Reliability Blueprint" y "Mastering Software Resilience"*
- **Métricas Clave:** Medir el **Defect Leakage** y estimar el **MTBF** (Mean Time Between Failures) del backend.
- **Diseño del Plan de Pruebas (.JMX):**
  - **Grupo de Hilos:** Simular un crecimiento gradual de usuarios concurrentes (Load Test) y ráfagas de peticiones rápidas (Stress Test).
  - **Flujo de Peticiones:**
    1. POST `/auth/login` (Extracción del JWT Token con JSON PostProcessor).
    2. GET `/hoteles` (Enviando JWT en cabecera `Authorization: Bearer`).
    3. POST `/reservations` (Simulación de creación de reservas concurrente).
  - **Aserciones:** Validar respuestas de éxito 200 y tiempos de respuesta (p95 < 500ms).

### E. Jenkins (Automatización e Integración Continua CI/CD)
*Basado en "Open Source DevSecOps Arsenal" y "Agile Software Blueprints"*
- **Filosofía Shift-Left:** Integrar las validaciones de calidad lo más temprano posible en la rama principal.
- **Definición del Pipeline (`Jenkinsfile`):**
  - **Stage 1 (Build):** Compilar backend (`mvn clean compile`) y frontend (`npm run build`).
  - **Stage 2 (Unit Tests):** Ejecutar suite de JUnit y generar reporte JaCoCo (`mvn test`).
  - **Stage 3 (SAST):** Escaneo estático rápido (ej. Snyk o SonarQube si está integrado).
  - **Stage 4 (Security Audit):** Ejecución automatizada de OWASP ZAP en modo básico.
  - **Stage 5 (Archivado):** Guardado automático de los artefactos de test (.exec de JaCoCo e informes HTML).

### F. Estándares, Ley N.º 29973 y ODS 10 (Inclusión)
*Basado en "Ingeniería de Software Inclusiva" e "IEEE 29148 sobre Calidad de Requisitos"*
- **ODS 10 (Reducción de las Desigualdades):** Garantizar la inclusión digital a través del desarrollo accesible de software, evitando la discriminación de personas con discapacidades visuales o motoras.
- **Ley N.º 29973 (Ley General de la Persona con Discapacidad en Perú):** El artículo 52 exige el acceso equitativo a los sistemas de información y de comunicación.
- **Estrategia Técnica de Accesibilidad (WCAG 2.2 Nivel AA):**
  - **Perceptible:** Contraste de colores adecuado (mínimo 4.5:1), y atributos `alt` descriptivos en imágenes.
  - **Operable:** Navegación por teclado fluida (sin trampas de foco) y orden lógico de elementos tabulables (`tabindex`).
  - **Comprensible:** Mensajes de validación de formulario claros.
  - **Robusto:** Marcado semántico HTML estándar en los templates de Angular (usar `<button>` y `<input>` nativos en vez de div simulados).

---

## 2. Plan de Desarrollo y Reparto de Tareas (Feedback)

### 💬 Opinión y Sugerencias sobre el Desglose del Grupo

El desglose planteado por el equipo es **coherente y cubre todos los vacíos** identificados en las auditorías iniciales. Sin embargo, para asegurar el éxito de la entrega sin sobrecargas, recomiendo las siguientes sugerencias:

1. **JUnit (Integrante 1 + Integrante 4):** JUnit y la refactorización deben trabajar de la mano. El **Integrante 4 (Refactorización)** debe coordinar estrechamente con el **Integrante 1 (JUnit)** para no romper las pruebas existentes y escribir pruebas unitarias de los nuevos bloques refactorizados bajo el enfoque **TDD** (Red-Green-Refactor).
2. **Criterios de Aceptación (Integrante 2):** Los criterios de aceptación deben seguir el estándar **POUR** (WCAG) y la norma **IEEE 29148**. Integrar una matriz de trazabilidad que demuestre explícitamente cómo cada requerimiento funcional aporta al **ODS 10**.
3. **Jenkins (Integrante 3):** Es crucial que trabaje sobre un ambiente local estable o contenedor antes de configurar el Pipeline definitivo en Jenkins para evitar errores de compilación por diferencia de versiones de Java (recordar que actualizamos a **Java 21**).
4. **Implementación UI (Integrante 5):** Apoyar al **Integrante 1** asegurándose de agregar etiquetas ARIA (`aria-label`, `aria-describedby`) en los componentes frontend modificados de Angular, resolviendo las observaciones de Lighthouse.

---

### 📋 Asignación Detallada de Tareas y Criterios de Aceptación

#### INTEGRANTE 1 — Pruebas Unitarias y Evidencias de Repositorio
- **Tareas:**
  - Escribir casos de prueba negativos, límites y validación de roles en la capa de servicios (`ReservationService`, `StaffUserService`).
  - Documentar en el informe el código fuente de los tests unitarios.
  - Extraer evidencias gráficas del repositorio Git (historial de commits, etiquetas `v3.0.0` y log de compilación).
- **Criterio de Aceptación:** Cobertura de la capa de servicio $\geq 85\%$ en JaCoCo y cero fallos en la suite de pruebas al ejecutar `mvn test`.

#### INTEGRANTE 2 — Estándares, Validación de Despliegue y Criterios
- **Tareas:**
  - Crear matriz de relación de estándares del proyecto indicando pertinencia con ODS 10 y Ley N.º 29973.
  - Estandarizar criterios de aceptación bajo la estructura Given-When-Then (IEEE 29148 / BDD).
  - Reunir el sustento de despliegue: acta de validación firmada y capturas funcionales de la versión v3.0.0 corriendo.
- **Criterio de Aceptación:** Documento de requisitos actualizado con matriz de trazabilidad y acta de conformidad del sistema adjunta en PDF.

#### INTEGRANTE 3 — Automatización en Jenkins y Redacción
- **Tareas:**
  - Diseñar el `Jenkinsfile` para el backend y frontend.
  - Configurar el build automatizado y la ejecución de JUnit en el servidor de Jenkins.
  - Consolidar e integrar los reportes de ZAP, Lighthouse y JUnit en el documento final.
  - Mejorar la redacción técnica del informe general del proyecto.
- **Criterio de Aceptación:** Pipeline de Jenkins ejecutándose en verde (exitoso) y generación automática de reportes de test y seguridad.

#### INTEGRANTE 4 — Refactorización de Software (Kenny)
- **Tareas:**
  - Analizar "Test Smells" y "Code Smells" en el backend y frontend.
  - Refactorizar métodos complejos y controladores para mejorar la mantenibilidad y legibilidad del código.
  - Asegurar compatibilidad estricta con Java 21 y Spring Boot 3.2.5.
- **Criterio de Aceptación:** Ausencia de código duplicado y métodos de más de 50 líneas en controladores; superación exitosa de los tests unitarios tras la refactorización.

#### INTEGRANTE 5 — ODS 10 e Inclusividad en Interfaz (UI)
- **Tareas:**
  - Resolver las observaciones de Lighthouse en Angular (`/dashboard` y `/auth/login`).
  - Reemplazar imágenes pesadas por WebP.
  - Incorporar etiquetas ARIA a elementos interactivos sin texto y asegurar contraste de colores de contraste $\geq 4.5:1$ (Ley N.º 29973).
- **Criterio de Aceptación:** Puntuación de Accesibilidad en Google Lighthouse $\geq 90/100$ en las vistas evaluadas.
