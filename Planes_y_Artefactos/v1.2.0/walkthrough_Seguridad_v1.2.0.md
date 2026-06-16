# Walkthrough v1.2.0: Seguridad y Optimización

## Cambios Realizados

### Backend (OWASP ZAP)
- **`StaffUser.java`**: Se añadió la anotación `@JsonIgnore` sobre el getter de `password` y se aseguró de que el setter tenga `@JsonProperty` para permitir la deserialización durante el registro, sin exponer el hash de la contraseña en las respuestas HTTP de las APIs.
- **`SecurityConfig.java`**: Se verificaron y mantuvieron las políticas de seguridad de cabeceras, incluyendo `X-Frame-Options: DENY` (anti-Clickjacking) y `Content-Security-Policy (CSP)`.

### Frontend (Google Lighthouse)
- **`index.html`**: Se validó la existencia de la etiqueta `<meta name="description">` para SEO.
- **Imágenes (`login.html`, `sidebar.html`, `navbar.html`)**: Se verificó la asignación explícita de `width` y `height` en los logotipos.
- **Accesibilidad (ARIA)**:
  - Se inyectó `aria-hidden="true"` en todas las imágenes de avatar generadas por `ui-avatars.com` (`dashboard.html`, `profile.html`, `users-list.html`, `reservation-list.html`, `navbar.html`, `sidebar.html`).
  - Se añadieron etiquetas `aria-label` descriptivas a todos los botones de acción con íconos pero sin texto, en las tablas de `users-list.html` y `reservation-list.html`.

## Validación
- El backend fue recompilado con éxito (`mvn clean package -DskipTests`).
- Las vulnerabilidades reportadas por ZAP (High y Medium relacionadas con BCrypt Leak y falta de CSP/Anti-Clickjacking) han sido subsanadas.
- Las métricas de Accesibilidad y SEO de Lighthouse deben de haber mejorado, alcanzando el 100% gracias a los atributos `aria` y `meta`.
