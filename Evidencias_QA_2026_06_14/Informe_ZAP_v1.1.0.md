# Informe de Auditoría de Seguridad - OWASP ZAP (DAST)
**Versión evaluada:** v1.1.0
**Fecha:** 14 de Junio de 2026
**Herramienta:** OWASP ZAP (Zed Attack Proxy)
**Tipo de Análisis:** Escaneo Activo (Active Scan)

Este informe detalla las vulnerabilidades encontradas durante la fase de Control de Calidad (QA) de seguridad dinámica en el sistema "Hotel Reservas UPN".

---

## 1. Resumen de Hallazgos

El escaneo generó alertas principalmente enfocadas en configuraciones de cabeceras HTTP y una vulnerabilidad específica de divulgación de datos en la capa de respuesta de las APIs.

| Nivel de Riesgo | Cantidad | Descripción |
| :---: | :---: | :--- |
| **🔴 Alto (High)** | 1 | Divulgación de hash crítico (BCrypt). |
| **🟠 Medio (Medium)** | 4 | Cabeceras de seguridad ausentes y configuraciones permisivas (CORS/Clickjacking). |
| **🟡 Bajo (Low)** | Varios | Advertencias informativas. |

---

## 2. Detalle de Vulnerabilidades (A resolver en v1.2.0)

### 🔴 Riesgo Alto
#### 1. Divulgación de Hash - BCrypt
- **Descripción:** Las respuestas de las peticiones REST (probablemente de los endpoints de autenticación o de usuarios) están devolviendo los hashes BCrypt de las contraseñas al lado del cliente.
- **Acción a tomar (v1.2.0):** Implementar la anotación `@JsonIgnore` en el campo de contraseña del modelo o utilizar Data Transfer Objects (DTOs) más estrictos en el backend para omitir por completo esta información en las respuestas JSON.

### 🟠 Riesgo Medio
#### 2. Cabecera Content Security Policy (CSP) no configurada
- **Descripción:** Ausencia de políticas que restrinjan desde qué dominios se pueden cargar recursos.
- **Acción a tomar (v1.2.0):** Configurar `http.headers().contentSecurityPolicy(...)` en Spring Security.

#### 3. Configuración Incorrecta Cross-Domain (CORS)
- **Descripción:** La aplicación expone sus APIs de manera muy permisiva a solicitudes de origen cruzado (`*`).
- **Acción a tomar (v1.2.0):** Restringir la política de CORS en los controladores para permitir únicamente el origen del Frontend (`http://localhost:4200`).

#### 4. Falta de cabecera Anti-Clickjacking
- **Descripción:** La aplicación no incluye la directiva `X-Frame-Options`, permitiendo que un atacante incruste el sitio en un `iframe` invisible.
- **Acción a tomar (v1.2.0):** Habilitar `http.headers().frameOptions().deny()` en la configuración de Spring Security.

#### 5. Falta atributo de integridad de recursos secundarios
- **Descripción:** Scripts u hojas de estilo externas (CDNs) se cargan sin verificar su hash (SRI).
- **Acción a tomar (v1.2.0):** Añadir atributos `integrity` a todos los enlaces de dependencias externas en el `index.html` de Angular.

---

## Conclusión de la Auditoría
La base funcional y la arquitectura están sólidas y operativas. Se ha establecido una **Línea Base de Seguridad (v1.1.0)** clara y precisa. Las vulnerabilidades detectadas son comunes en la fase inicial de desarrollo y se solucionarán de manera metódica en la siguiente iteración (v1.2.0), centrada exclusivamente en la fortificación de la arquitectura web.
