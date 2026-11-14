# Walkthrough de Remediación de Vulnerabilidades y Ejecución de Pruebas
**Proyecto:** Hotel Reservas UPN v3.0.0  
**Estado:** Remediado y Verificado con Éxito ✅  
**Resultado de Pruebas:** 80 / 80 Tests Pasados 🏆

---

## Cambios Realizados

### 1. Backend: Configuración de Seguridad (`SecurityConfig.java`)
- **Cabeceras de Seguridad:** Se habilitó `X-Content-Type-Options: nosniff`, se configuró HSTS para subdominios con una duración de 1 año (`max-age=31536000`), y se configuró un `Content-Security-Policy` restrictivo.
- **CORS Reforzado:** Se restringieron los métodos permitidos, se expuso explícitamente la cabecera `Authorization`, y se configuró un tiempo de preflight cache de 3600 segundos.

### 2. Backend: Propiedades del Servidor (`application.properties`)
- **Cookies Seguras:** Se configuraron `http-only=true`, `secure=false` (para compatibilidad en localhost) y `same-site=strict`.
- **Ocultamiento de Información:** Se deshabilitó el stacktrace, mensaje y errores de binding en las respuestas HTTP de error.
- **Jackson:** Se deshabilitó la serialización de fechas como timestamps Unix, asegurando respuestas con el estándar ISO-8601.

### 3. Backend: Servidor Tomcat embebido (`ApplicationConfig.java`)
- Se agregó el bean `WebServerFactoryCustomizer` para deshabilitar explícitamente el header `X-Powered-By` en el conector de Tomcat.

### 4. Frontend: Integridad de Recursos (`index.html`)
- Se agregó el atributo `crossorigin="anonymous"` al stylesheet de Google Fonts en el encabezado.

### 5. Nuevas Pruebas Unitarias para Cobertura (JaCoCo)
- **`JwtServiceTest.java`**: Verifica generación de tokens, extracción de claims y validación de tokens correctos y expirados.
- **`SecurityConfigTest.java`**: Verifica el correcto funcionamiento de las cabeceras HTTP (`X-Content-Type-Options`, `X-Frame-Options`, `Strict-Transport-Security`) y la validez de la configuración CORS.
- **`ReservationTest.java`**: Pruebas básicas del modelo de datos de reservas.

---

## Verificación y Ejecución de Pruebas Unitarias

Se ejecutó la suite completa de pruebas unitarias usando Maven:
```bash
mvn test
```

### Resultados de la Consola:
```text
[INFO] Running com.luxury.hotel.config.JwtServiceTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.luxury.hotel.config.SecurityConfigTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.luxury.hotel.model.ReservationTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
...
[INFO] Results:
[INFO] 
[INFO] Tests run: 80, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] --- jacoco:0.8.11:report (report) @ hotel-reservas-backend ---
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

---

## Archivos Afectados

1. [SecurityConfig.java](file:///e:/Personal/Estudios/UPN/Carreras%20universitarias/Ingeniería%20de%20Sistemas/Ciclos/Ciclo%2008%20-%202026%20-%20I/Hotel-Reservas-UPN-2/backend/src/main/java/com/luxury/hotel/config/SecurityConfig.java) (Modificado)
2. [ApplicationConfig.java](file:///e:/Personal/Estudios/UPN/Carreras%20universitarias/Ingeniería%20de%20Sistemas/Ciclos/Ciclo%2008%20-%202026%20-%20I/Hotel-Reservas-UPN-2/backend/src/main/java/com/luxury/hotel/config/ApplicationConfig.java) (Modificado)
3. [application.properties](file:///e:/Personal/Estudios/UPN/Carreras%20universitarias/Ingeniería%20de%20Sistemas/Ciclos/Ciclo%2008%20-%202026%20-%20I/Hotel-Reservas-UPN-2/backend/src/main/resources/application.properties) (Modificado)
4. [index.html](file:///e:/Personal/Estudios/UPN/Carreras%20universitarias/Ingeniería%20de%20Sistemas/Ciclos/Ciclo%2008%20-%202026%20-%20I/Hotel-Reservas-UPN-2/frontend/src/index.html) (Modificado)
5. [JwtServiceTest.java](file:///e:/Personal/Estudios/UPN/Carreras%20universitarias/Ingeniería%20de%20Sistemas/Ciclos/Ciclo%2008%20-%202026%20-%20I/Hotel-Reservas-UPN-2/backend/src/test/java/com/luxury/hotel/config/JwtServiceTest.java) (Nuevo)
6. [SecurityConfigTest.java](file:///e:/Personal/Estudios/UPN/Carreras%20universitarias/Ingeniería%20de%20Sistemas/Ciclos/Ciclo%2008%20-%202026%20-%20I/Hotel-Reservas-UPN-2/backend/src/test/java/com/luxury/hotel/config/SecurityConfigTest.java) (Nuevo)
7. [ReservationTest.java](file:///e:/Personal/Estudios/UPN/Carreras%20universitarias/Ingeniería%20de%20Sistemas/Ciclos/Ciclo%2008%20-%202026%20-%20I/Hotel-Reservas-UPN-2/backend/src/test/java/com/luxury/hotel/model/ReservationTest.java) (Nuevo)
