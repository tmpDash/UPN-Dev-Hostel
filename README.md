# 🏨 Sistema de Gestión Hotelera - Hotel Reservas UPN

Este proyecto es una aplicación Full-Stack profesional diseñada para la gestión de reservas, huéspedes y servicios de una cadena hotelera. Implementa una arquitectura moderna, segura y escalable.

## 🚀 Características Principales
- **Arquitectura N-Capas**: Separación clara de responsabilidades (Controller, Service, Repository, Model).
- **Seguridad Robusta**: Autenticación y Autorización basada en **JWT (JSON Web Tokens)** y Spring Security.
- **Diseño Premium**: Interfaz moderna en Angular con estética "Luxury Concierge".
- **Control de Acceso (RBAC)**: Gestión de permisos según roles (ADMIN, RECEPCIONISTA, SUPERVISOR, LIMPIEZA).
- **Documentación Interactiva**: API documentada con **Swagger UI**.

---

## 🛠️ Requerimientos del Sistema
Para ejecutar este proyecto localmente, asegúrese de tener instalado:
- **Java JDK 17** o superior.
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
2. Abra `src/main/resources/application.properties` y configure su `username` y `password` de MySQL.
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

## 🔑 Credenciales de Prueba (Password: 123456)
| Rol | Usuario |
| :--- | :--- |
| **Administrador** | `admin@hotelupn.com` |
| **Recepcionista** | `juan.perez@hotelupn.com` |
| **Supervisor** | `maria.garcia@hotelupn.com` |
| **Limpieza** | `ana.limpieza@hotelupn.com` |

---

## 🏗️ Estructura del Proyecto
- **Backend**: Java 17, Spring Boot 3, Spring Data JPA, Hibernate, Spring Security, JWT, Lombok, MySQL.
- **Frontend**: Angular, TypeScript, Bootstrap 5, SweetAlert2, RxJS.

---
*Desarrollado como parte del curso de Ingeniería de Sistemas - UPN.*
