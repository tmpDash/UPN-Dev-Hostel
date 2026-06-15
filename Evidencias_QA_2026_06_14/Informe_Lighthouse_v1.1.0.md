# Informe de Auditoría Frontend - Google Lighthouse
**Versión evaluada:** v1.1.0
**Fecha:** 14 de Junio de 2026
**Herramienta:** Google Lighthouse (Chrome DevTools)

Este informe detalla los resultados obtenidos durante la auditoría de calidad del frontend (Angular) del sistema Hotel Reservas UPN. Se evaluaron dos vistas principales: la página de acceso (pública) y el panel de control (privada).

---

## 1. Evaluación de Pantalla: Login (`/auth/login`)

| Categoría | Puntuación | Estado |
| :--- | :---: | :--- |
| **Mejores Prácticas (Best Practices)** | **100** | 🟢 Excelente. El código es moderno, usa HTTPS localmente y no tiene vulnerabilidades detectables en consola. |
| **Accesibilidad (Accessibility)** | **97** | 🟢 Sobresaliente. Se cumplen casi todos los estándares ARIA y contraste de color. |
| **SEO** | **91** | 🟢 Muy Bueno. El documento tiene título y etiquetas de indexación correctas. |
| **Rendimiento (Performance)** | **68** | 🟠 Aceptable. |

### Hallazgos y Oportunidades de Mejora (Login)
- **Carga de imágenes pesadas:** El factor principal que penaliza el rendimiento es el peso de las imágenes estáticas (`login-bg.png` pesa 745 KB y `logo.png` pesa 556 KB). 
- **Acción recomendada:** Comprimir estas imágenes usando formatos modernos como WebP o AVIF, y definir explícitamente atributos `width` y `height` en las etiquetas `<img>` para evitar cambios de diseño (Layout Shifts).

---

## 2. Evaluación de Pantalla: Dashboard (`/dashboard`)

| Categoría | Puntuación | Estado |
| :--- | :---: | :--- |
| **Mejores Prácticas (Best Practices)** | **100** | 🟢 Excelente. Se manejan correctamente los flujos de datos sin errores de consola. |
| **Accesibilidad (Accessibility)** | **79** | 🟠 Aceptable. |
| **SEO** | **73** | 🟠 Aceptable. |
| **Rendimiento (Performance)** | **66** | 🟠 Aceptable. |

### Hallazgos y Oportunidades de Mejora (Dashboard)
- **Falta de atributos `alt`:** Las imágenes de los avatares de usuario (`img.rounded-circle`) carecen de texto alternativo, lo cual afecta la experiencia para lectores de pantalla.
- **Nombres accesibles ausentes:** Algunos botones (como los íconos de acción) y barras de progreso no poseen el atributo `aria-label` descriptivo.
- **Optimización de Buscadores (SEO):** Falta la etiqueta `<meta name="description">` en el documento principal, y algunos enlaces del menú desplegable no tienen un destino `href` rastreable.
- **Carga de recursos:** Al igual que en el login, el logotipo principal carece de redimensionamiento adecuado, afectando el LCP (*Largest Contentful Paint*).

## Conclusión
La aplicación frontend cuenta con una estructura sumamente sólida en cuanto a **Mejores Prácticas**, obteniendo puntuación perfecta en ambas vistas. Para alcanzar la excelencia (+90) en las métricas de Rendimiento, Accesibilidad y SEO en futuras iteraciones, se priorizará la optimización de imágenes (WebP) y la adición exhaustiva de atributos ARIA.
