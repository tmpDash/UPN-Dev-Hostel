# Walkthrough de la Implementación v1.2.0

¡Felicidades! La versión 1.2.0 de la aplicación ha sido fortificada con éxito, abordando todos los reportes del Control de Calidad Dinámico (DAST) y Análisis Estático de Rendimiento y Accesibilidad.

## Cambios Implementados en el Backend (Seguridad)
1. **Ocultamiento de Contraseñas (BCrypt Hash Leak):**
   Implementamos la anotación `@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)` en el modelo `StaffUser`. Esto le asegura al sistema que la contraseña puede recibirse para registro o login, pero **jamás** será serializada hacia el frontend.
2. **Defensas Anti-Hackers (Spring Security):**
   Integramos un esquema de cabeceras HTTP que defiende a la app de dos ataques graves:
   - **Clickjacking:** Habilitado `X-Frame-Options: DENY`, bloqueando la app en iframes externos.
   - **Cross-Site Scripting (XSS):** Habilitada `Content-Security-Policy (CSP)` estricta para definir dominios de confianza explícitos.

## Cambios Implementados en el Frontend (Lighthouse)
1. **Prevención de CLS (Cumulative Layout Shift):**
   Declaramos estrictamente los parámetros `width` y `height` en todas las imágenes del logotipo (`logo.png`) para mejorar la métrica de Performance (Rendimiento).
2. **Atributos ARIA (Accesibilidad):**
   Inyectamos etiquetas `alt` en todos los avatares circulares del sistema (Dashboard, lista de clientes y usuarios).
3. **Métrica SEO Perfecta:**
   Inyectamos `<meta name="description">` en la cabecera `index.html`.

## Verificación
> [!NOTE]
> Las 69 pruebas unitarias se ejecutaron tras los cambios con resultado **BUILD SUCCESS**, certificando que las cabeceras de seguridad y las reglas de protección no rompieron el funcionamiento existente de los controladores ni de la base de datos simulada.
