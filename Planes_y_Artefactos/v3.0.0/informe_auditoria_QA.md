# Informe de Auditoría QA — Hotel Reservas UPN
**Versión evaluada:** v3.0.0  
**Fecha de escaneo:** 16 de junio de 2026  
**Herramientas utilizadas:** ZAP by Checkmarx 2.17.0 · JaCoCo 0.8.11 · Google Lighthouse

---

## Resumen Ejecutivo

| Herramienta | Resultado |
| :--- | :--- |
| **ZAP (Seguridad)** | ✅ 0 Alertas Altas · ⚠️ 4 Alertas Medias · 🔵 11 Alertas Bajas · ℹ️ 8 Informativas |
| **JaCoCo (Cobertura)** | ⚠️ 39% instrucciones · 7% ramas — Capa de servicio: 92% ✅ |
| **Lighthouse Login** | Performance 68/100 — pendiente optimización de imágenes |
| **Lighthouse Dashboard** | Accessibility 79/100 — pendiente atributos ARIA |

---

## 1. Seguridad — OWASP ZAP (Escaneo del 16-Jun-2026)

### 1.1 Resumen de Alertas

| Nivel | Cantidad | Estado |
| :--- | :---: | :--- |
| 🔴 Alto | **0** | ✅ Sin alertas críticas |
| 🟠 Medio | **4** | ⚠️ Requiere atención |
| 🟡 Bajo | **11** | 🔵 Mejoras recomendadas |
| ℹ️ Informativo | **8** | Sin impacto directo |

### 1.2 Alertas Medias — PENDIENTES de corrección

| # | ID Plugin | Vulnerabilidad | Instancias | Acción Requerida |
| :---: | :--- | :--- | :---: | :--- |
| 1 | `10038` | **Cabecera Content Security Policy (CSP) no configurada** | 1 | Agregar cabecera `Content-Security-Policy` en el servidor/proxy frontend |
| 2 | `10098` | **Configuración Incorrecta Cross-Domain** | 4 | Revisar reglas CORS; el origen permitido no debe ser wildcard `*` en producción |
| 3 | `90003` | **Falta atributo de integridad de recursos secundarios (SRI)** | 1 | Agregar atributos `integrity` y `crossorigin` a recursos de CDNs externos |
| 4 | `10020` | **Falta de cabecera Anti-Clickjacking (X-Frame-Options)** | 1 | Añadir `X-Frame-Options: DENY` o `SAMEORIGIN` en respuesta HTTP del frontend |

> [!WARNING]
> Las 4 alertas medias son correcciones de cabeceras HTTP que se aplican en el servidor o proxy (Nginx/Apache). No implican cambios en lógica de negocio pero son requeridas para superar auditorías de seguridad en producción.

### 1.3 Alertas Bajas — PENDIENTES de revisión

| # | ID Plugin | Vulnerabilidad | Instancias | Acción Recomendada |
| :---: | :--- | :--- | :---: | :--- |
| 1 | `10010` | Cookie Sin Flag `HttpOnly` | 1 | Añadir `HttpOnly` a las cookies de sesión |
| 2 | `10011` | Cookie Sin Flag `Secure` | 3 | Añadir `Secure` a cookies (requiere HTTPS) |
| 3 | `10054` | Cookie con atributo `SameSite=None` | 1 | Cambiar a `SameSite=Strict` o `Lax` si aplica |
| 4 | `10054` | Cookie sin atributo `SameSite` | 3 | Añadir `SameSite=Strict` explícitamente |
| 5 | `10096` | Divulgación de Marcas de Tiempo Unix | Sistémica | Suprimir timestamps en respuestas públicas |
| 6 | `10037` | Header `X-Powered-By` expuesto | 2 | Eliminar el header `X-Powered-By` en el servidor |
| 7 | `10036` | Header `Server` con versión expuesta | 1 | Ocultar versión en el header `Server` |
| 8 | `10061` | Header `X-AspNet-Version` expuesto | 4 | Eliminar (no aplica a Spring Boot, revisar proxy) |
| 9 | `10021` | Falta cabecera `X-Content-Type-Options` | Sistémica | Añadir `X-Content-Type-Options: nosniff` |
| 10 | `10035` | `Strict-Transport-Security` deshabilitado | 1 | Habilitar HSTS (solo en producción con HTTPS) |
| 11 | `10035` | `Strict-Transport-Security` Header no establecido | Sistémica | Configurar `Strict-Transport-Security: max-age=31536000` |

### 1.4 Reglas que PASAN (sin hallazgos críticos)

> [!NOTE]
> Las siguientes pruebas activas se ejecutaron y no encontraron vulnerabilidades: SQL Injection, XSLT Injection, CRLF Injection, XPath Injection, Remote OS Command Injection, XML External Entity (XXE), Remote File Inclusion, Server Side Template Injection (SSTI), Buffer Overflow, Spring Actuator Leak, Swagger UI Vulnerability Detector, Parameter Tampering, Server Side Include.

---

## 2. Cobertura de Código — JaCoCo (Backend v3.0.0)

### 2.1 Resumen Global

| Métrica | Perdidas | Total | Cobertura |
| :--- | :---: | :---: | :---: |
| **Instrucciones** | 2,035 | 3,363 | **39%** ⚠️ |
| **Ramas (Branches)** | 334 | 360 | **7%** 🔴 |
| **Complejidad Ciclomática** | 264 | 458 | — |
| **Líneas** | 117 | 391 | — |
| **Métodos** | 95 | 278 | — |
| **Clases** | 5 | 32 | — |

### 2.2 Cobertura por Paquete

| Paquete | Instrucciones Cob. | Ramas Cob. | Estado |
| :--- | :---: | :---: | :--- |
| `com.luxury.hotel.controller` | **98%** | **100%** | ✅ Excelente |
| `com.luxury.hotel.service` | **92%** | **78%** | ✅ Buena |
| `com.luxury.hotel` (main) | **37%** | n/a | 🔵 Aceptable |
| `com.luxury.hotel.model` | **31%** | **0%** | ⚠️ Baja |
| `com.luxury.hotel.dto` | **19%** | **0%** | ⚠️ Baja |
| `com.luxury.hotel.config` | **1%** | **0%** | 🔴 Crítica |

### 2.3 Observaciones y Pendientes — JaCoCo

> [!IMPORTANT]
> La cobertura de ramas (branches) a nivel global es solo del **7%**. Esto indica que la mayoría de las pruebas no ejercitan los caminos condicionales. Se requiere crear pruebas que cubran estos escenarios.

**Acciones pendientes:**

1. **`com.luxury.hotel.config` (1%):** Crear pruebas de integración para `SecurityConfig` y `JwtConfig`. Son las más críticas en términos de seguridad.
2. **`com.luxury.hotel.dto` (19%):** Agregar pruebas para constructores, builders y validaciones de DTOs (`AuthResponse`, `LoginRequest`, `ReportStatsDTO`).
3. **`com.luxury.hotel.model` (31%, 0% ramas):** Agregar pruebas básicas de entidades JPA (getters/setters, constructores, validaciones).
4. **Cobertura de ramas global:** Aumentar pruebas de casos negativos y borde en capa `service` (actualmente 78% de ramas).

---

## 3. Rendimiento Frontend — Google Lighthouse (Frontend v3.0.0)

### 3.1 Login (`/auth/login`)

| Categoría | Puntuación | Estado |
| :--- | :---: | :--- |
| **Best Practices** | 100 | ✅ Excelente |
| **Accessibility** | 97 | ✅ Sobresaliente |
| **SEO** | 91 | ✅ Muy Bueno |
| **Performance** | 68 | 🟠 Aceptable |

**Pendientes Login:**
- Comprimir imágenes (`login-bg.png` 745 KB, `logo.png` 556 KB) a formato WebP/AVIF
- Definir atributos `width` y `height` explícitos en `<img>` para evitar Layout Shifts (CLS)

### 3.2 Dashboard (`/dashboard`)

| Categoría | Puntuación | Estado |
| :--- | :---: | :--- |
| **Best Practices** | 100 | ✅ Excelente |
| **Accessibility** | 79 | 🟠 Aceptable |
| **SEO** | 73 | 🟠 Aceptable |
| **Performance** | 66 | 🟠 Aceptable |

**Pendientes Dashboard:**
- Agregar atributo `alt` a todos los avatares de usuario (`img.rounded-circle`)
- Añadir `aria-label` descriptivos a botones de iconos y barras de progreso
- Incluir `<meta name="description">` en el documento principal
- Asegurar que los enlaces del menú desplegable tengan `href` rastreable
- Optimizar LCP corrigiendo el tamaño del logotipo principal

---

## 4. Plan de Acción Priorizado

### Prioridad Alta (para siguiente release)

| # | Ítem | Área |
| :---: | :--- | :--- |
| 1 | Agregar cabeceras de seguridad HTTP: CSP, X-Frame-Options, X-Content-Type-Options, HSTS | Backend/Proxy |
| 2 | Corregir atributos de cookies: HttpOnly, Secure, SameSite=Strict | Backend (Spring Security) |
| 3 | Ocultar headers informativos: X-Powered-By, Server, X-AspNet-Version | Backend/Proxy |
| 4 | Crear pruebas unitarias para `com.luxury.hotel.config` | Backend (Testing) |

### Prioridad Media

| # | Ítem | Área |
| :---: | :--- | :--- |
| 5 | Agregar SRI (integrity + crossorigin) a recursos CDN externos | Frontend |
| 6 | Optimizar imágenes de login a WebP (login-bg, logo) | Frontend |
| 7 | Aumentar cobertura de DTOs y Modelos (objetivo ≥ 60%) | Backend (Testing) |
| 8 | Cubrir ramas condicionales en capa Service (objetivo ≥ 85%) | Backend (Testing) |

### Prioridad Baja (mejoras continuas)

| # | Ítem | Área |
| :---: | :--- | :--- |
| 9 | Agregar `alt`, `aria-label` en avatares y botones del Dashboard | Frontend |
| 10 | Agregar `<meta name="description">` al index.html | Frontend |
| 11 | Asegurar `href` en enlaces del menú desplegable | Frontend |

---

*Generado a partir de: `2026-06-16-ZAP-Report-.html` · `jacoco_v1.4.0.zip` · `Lighthouse_Login_v1.4.0.pdf` · `Lighthouse_Dashboard_v1.4.0.pdf`*  
*Informe creado: 2026-07-02*
