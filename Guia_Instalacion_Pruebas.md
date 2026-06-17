# 🚀 Guía Rápida de Instalación y Pruebas (Hotel Luxury)

Esta guía está diseñada para que cualquier desarrollador o tester del equipo pueda levantar el proyecto localmente en su máquina en menos de 10 minutos.

---

## 1. Requisitos Previos e Instalación

Para ejecutar la aplicación localmente (Frontend y Backend), necesitas tener instaladas las siguientes herramientas. 

### ¿Cómo verificar si ya los tengo instalados?
Abre una terminal (CMD, PowerShell o Git Bash) y ejecuta los siguientes comandos:

| Herramienta | Comando de Verificación | Resultado Esperado | Si no lo tienes... |
| :--- | :--- | :--- | :--- |
| **Java JDK** | `java -version` | `java version "21.x.x"` (o superior) | [Descargar JDK 21](https://adoptium.net/) |
| **Node.js** | `node -v` | `v20.x.x` (o superior) | [Descargar Node.js](https://nodejs.org/es/) |
| **NPM** | `npm -v` | `10.x.x` (o superior) | Se instala junto a Node.js |
| **Angular CLI**| `ng version` | `Angular CLI: 17.x` o superior | Corre: `npm install -g @angular/cli` |
| **MySQL** | `mysql -V` | `mysql  Ver 8.0.x` | [Descargar MySQL](https://dev.mysql.com/downloads/installer/) |

> [!NOTE]
> Si el comando `mysql -V` muestra el error *"El término 'mysql' no se reconoce..."*, significa que MySQL sí puede estar instalado pero no está configurado en las variables de entorno (PATH) de Windows. Puedes ignorar este error y verificar si tienes **MySQL Workbench**, **DBeaver** o **XAMPP** instalados.

---

## 2. Entornos de Desarrollo (IDEs) Recomendados

Puedes abrir el proyecto completo en cualquier editor de texto, pero para máxima productividad se recomiendan los siguientes:

*   **Para el Frontend (Angular):** **Visual Studio Code (VS Code)**.
    *   *Extensiones recomendadas:* Angular Language Service, Tailwind CSS IntelliSense.
*   **Para el Backend (Spring Boot / Java):** **IntelliJ IDEA (Community Edition)**, **Eclipse**, o el mismo **VS Code** (con el "Extension Pack for Java").

---

## 3. Configuración de la Base de Datos (MySQL)

1. Abre tu gestor de base de datos favorito (MySQL Workbench, DBeaver, XAMPP, etc.).
2. Conéctate a tu servidor local de MySQL (por defecto en el puerto `3306`).
3. Crea una base de datos vacía ejecutando el siguiente comando SQL:
   ```sql
   CREATE DATABASE db_hotel_reservas;
   ```
   *(Nota: No necesitas crear tablas ni registros, el sistema de Spring Boot creará la estructura y poblará los datos iniciales de forma automática al arrancar).*

---

## 4. Levantar el Backend (Spring Boot)

El backend expone la API REST que necesita el frontend para funcionar. Debe levantarse primero.

1. Abre la carpeta del proyecto y navega hacia la ruta del backend: `\Hotel-Reservas-UPN-2\backend`.
2. **Configura tus credenciales:**
   * Abre el archivo: `backend\src\main\resources\application.properties`.
   * Busca las líneas de `spring.datasource.username` y `spring.datasource.password`.
   * Cambia los valores por el usuario y contraseña de tu servidor MySQL local (usualmente usuario `root` y clave vacía o `root`).
3. **Ejecuta el backend:**
   Abre una terminal en la carpeta `backend` y ejecuta:
   ```bash
   mvn spring-boot:run
   ```
   *(Si usas IntelliJ IDEA, simplemente busca la clase `HotelReservasApplication.java` y dale click derecho -> "Run").*
4. **Verificación:** Si todo está bien, verás en la consola que la aplicación arrancó en el puerto **8087**. Puedes ver la documentación de la API entrando a:
   👉 `http://localhost:8087/api/swagger-ui/index.html`

---

## 5. Levantar el Frontend (Angular)

1. Abre una *nueva* terminal y navega a la carpeta del frontend: `\Hotel-Reservas-UPN-2\frontend`.
2. **Instala las dependencias** (Solo debes hacerlo la primera vez):
   ```bash
   npm install
   ```
3. **Ejecuta el servidor de Angular:**
   ```bash
   npm start
   ```
4. **Verificación:** La terminal indicará que compiló exitosamente. Abre tu navegador y entra a:
   👉 `http://localhost:4200`

---

## 6. Credenciales de Prueba para QA

Una vez que estés en la pantalla de inicio de sesión (`localhost:4200/login`), puedes utilizar cualquiera de estas cuentas precargadas en el sistema para realizar pruebas según el nivel de acceso (Rol):

| Perfil / Rol | Correo Electrónico | Contraseña |
| :--- | :--- | :--- |
| **Gerencia (Admin)** | `admin@luxuryhotel.com` | `123456` |
| **Recepcionista** | `juan.perez@luxuryhotel.com` | `123456` |
| **Supervisor** | `maria.garcia@luxuryhotel.com` | `123456` |
| **Limpieza** | `ana.limpieza@luxuryhotel.com` | `123456` |

¡Todo listo! El sistema ya está operativo en la máquina local para realizar las validaciones necesarias.
