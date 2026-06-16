# Plan de Implementación de la Versión 1.2.0: Seguridad y Optimización (QA)

Este plan aborda de manera definitiva las vulnerabilidades encontradas por OWASP ZAP (Fase DAST) y las oportunidades de mejora detectadas por Google Lighthouse en el frontend, cerrando así la validación del sistema para su paso a la fase de pruebas automatizadas E2E.

## 🔴 User Review Required
Por favor, revisa las configuraciones de seguridad propuestas (CORS, CSP) y confírmame si estás de acuerdo con aplicar `X-Frame-Options: DENY` que bloquea el uso de esta app dentro de iframes externos. 

## Cambios Propuestos

---

### Backend (OWASP ZAP)

#### [MODIFY] `StaffUser.java`
- Se añadirá la anotación `@JsonIgnore` sobre el campo `password` para evitar que el hash BCrypt se filtre en las respuestas HTTP (Resolviendo la vulnerabilidad de riesgo **Alto** de ZAP).

#### [MODIFY] `SecurityConfig.java`
- Se añadirán las cabeceras de seguridad estrictas en el `SecurityFilterChain`:
  - `X-Frame-Options: DENY` para mitigar ataques de **Clickjacking**.
  - `Content-Security-Policy (CSP)` para mitigar ataques **XSS**, permitiendo únicamente orígenes seguros (localhost, fonts.googleapis, ui-avatars).

---

### Frontend (Google Lighthouse)

#### [MODIFY] `index.html`
- Se añadirá la etiqueta `<meta name="description" content="Hotel Reservas UPN - Sistema de Gestión">` en el `<head>` para subir la métrica SEO al 100%.

#### [MODIFY] Vistas con Imágenes (`login.html`, `sidebar.html`, `navbar.html`)
- Se agregarán y forzarán atributos explícitos `width` y `height` en los logotipos (`logo.png`) para evitar los "Layout Shifts" detectados por Lighthouse, mejorando la métrica de Performance.

#### [MODIFY] Vistas del Dashboard (Accesibilidad ARIA)
- Se inyectarán atributos `alt` descriptivos y/o `aria-hidden="true"` a todas las imágenes circulares generadas por `ui-avatars.com` (`img.rounded-circle`).
- Se añadirán `aria-label` a los botones de acción sin texto (solo íconos) descubiertos en los listados del dashboard.

---

## Plan de Verificación

### Pruebas de Verificación
- Compilaremos nuevamente el proyecto Spring Boot (`mvn clean package`) para garantizar que la configuración de seguridad sea válida.
- Lanzaremos ZAP nuevamente apuntando a `localhost:4200` y confirmaremos que los avisos de "Clickjacking" y "CSP" desaparezcan.
- Lanzaremos Lighthouse en el Dashboard para validar que la métrica de SEO y Accesibilidad alcance o supere el 95.
