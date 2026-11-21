# 🏨 Sistema de Gestión Hotelera - Hotel Luxury

Este proyecto es una solución **Full-Stack empresarial** diseñada para automatizar y optimizar la gestión integral de una cadena hotelera. Permite administrar de forma centralizada sedes hoteleras, habitaciones, reservas de huéspedes, catálogo de servicios adicionales y el control del personal operativo y administrativo.

---

## 📖 ¿De qué trata el proyecto? (Visión General)

El sector hotelero moderno requiere una sincronización fluida entre la atención en mostrador (recepción), la prestación de comodidades (amenities/servicios) y la toma de decisiones gerenciales. **Hotel Luxury** resuelve esta necesidad eliminando registros manuales propensos a error, calculando automáticamente costos por estancia y consumos, y garantizando la trazabilidad y seguridad en cada operación.

### 🎯 Problemas clave que resuelve
1. **Pérdida de control en consumos adicionales:** Integra en una sola cuenta los servicios consumidos por el huésped (spa, restaurante, movilidad, lavandería) junto con el costo del hospedaje.
2. **Inconsistencias en disponibilidad y reservas:** Valida fechas de estancia (check-in y check-out) e impide estados lógicos erróneos.
3. **Seguridad y segregación de funciones:** Evita que personal no autorizado modifique tarifas o acceda a reportes financieros mediante un control estricto de accesos por roles.
4. **Visibilidad del negocio:** Proporciona indicadores claros de ocupación mensual y servicios más rentables.

---

## 🧩 Módulos y Funcionalidades del Sistema

### 1. 🏨 Gestión de Sedes Hoteleras
* Catálogo de hoteles de la cadena (ej. Sedes Lima, Cusco, Arequipa, Trujillo, Iquitos).
* Registro de capacidad física, cantidad total de habitaciones, dirección y ciudad.

### 2. 📅 Motor de Reservas y Facturación
* **Registro de Estancia:** Captura de datos del huésped, fechas de entrada (*Check-in*) y salida (*Check-out*).
* **Tipos de Habitación:** Soporte para habitaciones `SIMPLE`, `DOBLE` y `SUITE`.
* **Cálculo Dinámico de Tarifas:** Computa automáticamente el costo por noches de hospedaje sumado a los servicios adicionales cargados a la habitación.
* **Ciclo de Estados de Reserva:**
  * `PENDIENTE`: Registro inicial a la espera de confirmación o pago.
  * `CONFIRMADA`: Reserva garantizada lista para recepción.
  * `COMPLETADA`: Estancia finalizada con liquidación total de consumos (*Check-out*).
  * `CANCELADA`: Anulación de la reserva liberando la disponibilidad.

### 3. 🛎️ Catálogo de Servicios Adicionales (Upselling & Amenities)
* Gestión de amenidades organizadas por categorías de negocio:
  * **FOOD:** Restaurante Gourmet, Room Service 24h.
  * **WELLNESS:** Spa & Sauna terapéutico, Piscina Climatizada.
  * **TRANSPORT:** Traslados privados al aeropuerto, Tours arqueológicos.
  * **GENERAL:** Lavandería express, Wi-Fi Premium, Alquiler de bicicletas.
* Vinculación directa a reservas activas especificando cantidad consumida y precio congelado al momento del consumo.

### 4. 📊 Dashboard Analítico y Reportes Gerenciales
* Métricas operativas en tiempo real:
  * Tasa de ocupación mensual proyectada e histórica.
  * Desglose de ingresos por categoría de servicios adicionales.
  * Indicadores de reservas activas, pendientes y canceladas.

### 5. 👥 Gestión de Personal y Control de Acceso (RBAC)
Cada usuario pertenece a un perfil con permisos específicos para proteger la integridad operativa:

| Rol | Alcance y Responsabilidades |
| :--- | :--- |
| **Administrador (`ADMIN`)** | Control total del sistema: gestión de usuarios/staff, configuración de hoteles, supervisión de reservas, catálogo de servicios y acceso a reportes financieros. |
| **Supervisor (`SUPERVISOR`)** | Supervisión operativa: consulta y auditoría de reservas, catálogo de servicios e inspección de reportes de ocupación y rendimiento. |
| **Recepcionista (`RECEPCIONISTA`)** | Operación diaria en mostrador: registro de nuevos huéspedes, creación y edición de reservas, adición de consumos/servicios a reservas y gestión de check-in / check-out. |
| **Personal de Limpieza (`LIMPIEZA`)** | Soporte operativo: visualización del panel general y seguimiento del estado operativo de las habitaciones e instalaciones. |

---

## 🚀 Características Técnicas Principales
- **Arquitectura N-Capas**: Separación estricta de responsabilidades en backend (Controller, Service, Repository, DTO, Model).
- **Seguridad Robusta**: Autenticación y Autorización basada en **JWT (JSON Web Tokens)** con Spring Security stateless.
- **Diseño Premium**: Interfaz moderna en Angular con estética *Luxury Concierge* (TailwindCSS + Preline UI).
- **Documentación Interactiva**: API RESTful completamente documentada e interactiva con **Swagger / OpenAPI 3.0**.

---

## 🌐 Despliegue en Producción

| Componente | Plataforma | URL / Configuración |
| :--- | :--- | :--- |
| **Frontend Angular** | Cloudflare Workers (Static Assets) | [upn-dev-hostel.archer-2312.workers.dev](https://upn-dev-hostel.archer-2312.workers.dev) |
| **Backend Spring Boot** | Render | [hotel-luxury-api.onrender.com/api](https://hotel-luxury-api.onrender.com/api) |
| **Swagger / OpenAPI** | Render | [Documentación de la API](https://hotel-luxury-api.onrender.com/api/swagger-ui/index.html) |
| **Base de datos MySQL 8.4** | Aiven | Servicio administrado; las credenciales se almacenan como variables de entorno en Render. |

### Configuración aplicada

- El frontend se compila desde `frontend/` y consume `https://hotel-luxury-api.onrender.com/api` mediante el environment de producción.
- `wrangler.jsonc` publica `frontend/dist/hotel-reservas-web/browser` y configura el fallback de rutas para la SPA de Angular.
- Cloudflare ejecuta `npm --prefix frontend ci && npm --prefix frontend run build` y despliega con `npx wrangler deploy` por cada actualización de `main`.
- Render utiliza `DB_URL`, `DB_USER`, `DB_PASSWORD` y `JWT_SECRET` sin exponer secretos en el repositorio.
- Los orígenes autorizados se configuran en Render mediante `CORS_ALLOWED_ORIGINS`. El origen de producción actual es `https://upn-dev-hostel.archer-2312.workers.dev`.
- La autenticación utiliza JWT y el interceptor de Angular envía el token como `Authorization: Bearer <token>`.
- Los datos iniciales de hoteles, servicios, usuarios y reservas se encuentran en `backend/src/main/resources/import.sql` y fueron cargados en Aiven.

> **Arranque en frío de Render:** el plan gratuito suspende el backend después de un periodo sin actividad. La primera solicitud o el primer inicio de sesión puede tardar aproximadamente entre 45 y 90 segundos mientras la API vuelve a iniciar. Las solicitudes posteriores funcionan normalmente.

### Validación del despliegue

- Compilación Angular de producción completada correctamente.
- Suite backend: **80 pruebas aprobadas, 0 fallos**.
- Login, emisión de JWT, rutas SPA, imágenes y acceso al dashboard validados desde la URL pública.

---

## 🛠️ Requerimientos del Sistema
Para ejecutar este proyecto localmente, asegúrese de tener instalado:
- **Java JDK 21** o superior.
- **Node.js 20.x** (LTS) y **npm**.
- **Angular CLI** (`npm install -g @angular/cli`).
- **MySQL Server 8.0** o superior.
- **Maven** (opcional, integrado en la mayoría de IDEs).

---

## 📦 Instalación y Configuración

### 1. Base de Datos
Cree la base de datos en su servidor MySQL local:
```sql
CREATE DATABASE db_hotel_reservas;
```

### 2. Backend (Spring Boot)
1. Navegue a la carpeta `backend/`.
2. Configure las variables de entorno `DB_URL`, `DB_USER`, `DB_PASSWORD` y `JWT_SECRET` para su instancia local de MySQL.
3. Ejecute el proyecto:
   ```bash
   mvn spring-boot:run
   ```
*La API estará disponible en: `http://localhost:8087/api`*
*Swagger UI: `http://localhost:8087/api/swagger-ui/index.html`*

### 3. Frontend (Angular)
1. Navegue a la carpeta `frontend/`.
2. Instale las dependencias:
   ```bash
   npm install
   ```
3. Inicie el servidor de desarrollo:
   ```bash
   npm start
   ```
*La aplicación estará disponible en: `http://localhost:4200`*

---

## 🧪 Calidad y Pruebas (QA)
Este proyecto enfatiza el aseguramiento de la calidad mediante pruebas integrales:

### Pruebas Unitarias (Backend - v1.1.0)
- **Framework:** JUnit 5 + Mockito.
- **Cobertura:** Validada mediante JaCoCo.
- **Alcance Actual:** Capa de Controladores y Capa de Servicios (Reglas de negocio aisladas).
- **Resultado:** 69 pruebas ejecutadas con éxito (`BUILD SUCCESS`).

### Auditoría Frontend (Lighthouse - v1.1.0)
Se auditaron las interfaces del sistema obteniendo los siguientes resultados base:
- **Login:** Mejores Prácticas (100), Accesibilidad (97), SEO (91), Rendimiento (68).
- **Dashboard:** Mejores Prácticas (100), Accesibilidad (79), SEO (73), Rendimiento (66).
- *Conclusión:* Excelente arquitectura de mejores prácticas. Las métricas de rendimiento y accesibilidad brindan una línea base clara para futuras optimizaciones de imágenes (WebP) y etiquetas ARIA.

### Pruebas de Seguridad (ZAP - v1.1.0)
Se realizó un *Escaneo Activo* automatizado, estableciendo la línea base de seguridad de la versión 1.1.0:
- **Riesgo Alto:** 1 hallazgo (Divulgación accidental de hash BCrypt en las respuestas de las APIs).
- **Riesgo Medio:** 4 hallazgos (Cabeceras de seguridad ausentes como CSP, protección Anti-Clickjacking y CORS permisivo).
- *Conclusión:* Las vulnerabilidades detectadas son propias de una fase temprana de desarrollo. Quedan documentadas para ser fortificadas y resueltas integralmente en la próxima iteración (v1.2.0) a través de Spring Security.

### Correcciones de Seguridad y Optimización (v1.2.0)
- **Seguridad ZAP Resoluciones:**
  - Se implementó `@JsonIgnore` y `@JsonProperty` en la entidad `StaffUser` para ocultar la fuga de hashes BCrypt detectada como Riesgo Alto.
  - Se configuró estrictamente el encabezado `X-Frame-Options: DENY` y una sólida directiva de `Content-Security-Policy (CSP)` en `SecurityConfig.java`.
- **Rendimiento y Accesibilidad Lighthouse Resoluciones:**
  - Se implementó `aria-hidden="true"` en avatares generados y etiquetas `aria-label` en botones de acción sin texto.
  - Las nuevas pruebas confirmaron que no hubo impacto en las funciones base (69/69 Unit Tests passed).

### Refactorización Comercial / White-label (v1.3.0)
- **Migración de Marca:** Se renombraron los paquetes Java (com.luxury.hotel) eliminando dependencias universitarias.
- **Identidad Frontend:** Se actualizó el título y metadatos de Angular a Hotel Luxury.
- **Datos Saneados:** El archivo de inicialización de la BD ahora utiliza cuentas corporativas (@luxuryhotel.com). Todo testeado en verde (69/69 Unit Tests).

### Rediseño Visual, Traducciones y Auditoría Final (v1.4.0)
- **UI/UX Moderno:** Se migró a un diseño web basado en utilidades modernas (TailwindCSS / Preline), colores corporativos elegantes, y componentes responsivos en todos los módulos (Login, Dashboard, Reservas, Servicios).
- **Internacionalización:** Traducción completa del frontend de inglés a español, abarcando menús, formularios y la página de "Mi Perfil".
- **Correcciones Estables:** Se solucionó un problema de enlace asíncrono y Change Detection en Angular que prevenía la renderización del Catálogo de Servicios al crear nuevas reservas.
- **Auditoría Técnica:**
  - **Lighthouse:** Se validaron los rediseños con puntajes sobresalientes (Accesibilidad 92/100, SEO 100/100, Mejores Prácticas 96/100).
  - **OWASP:** Revisión de código en Spring Security (`SecurityConfig.java`) confirmó correcta mitigación contra CORS, CSRF (JWT Stateless) y XFO/Clickjacking.
  - **Unit Tests:** Validado nuevamente (69/69 Unit Tests passed), con 98% de cobertura en controladores y 92% en servicios.

**Ejecutar pruebas y generar reporte de cobertura:**
```bash
cd backend
mvn clean test jacoco:report
```
*El reporte HTML se generará en la ruta: `backend/target/site/jacoco/index.html`*

---

## 🔑 Credenciales de Prueba (Password: 123456)
| Rol | Usuario |
| :--- | :--- |
| **Administrador** | `admin@luxuryhotel.com` |
| **Recepcionista** | `juan.perez@luxuryhotel.com` |
| **Supervisor** | `maria.garcia@luxuryhotel.com` |
| **Limpieza** | `ana.limpieza@luxuryhotel.com` |

---

## 🏗️ Estructura del Proyecto
- **Backend**: Java 21, Spring Boot 3, Spring Data JPA, Hibernate, Spring Security, JWT, Lombok, MySQL.
- **Frontend**: Angular, TypeScript, TailwindCSS, Preline UI, SweetAlert2, RxJS.
- **IA/ML**: Python, pandas y scikit-learn para pronóstico global de ocupación a 7 y 30 días. Consulte [ml/README.md](ml/README.md).

---

