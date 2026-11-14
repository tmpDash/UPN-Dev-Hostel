# Plan de Mitigación de Vulnerabilidades — Hotel Reservas UPN
**Versión:** v3.0.0  
**Fecha del escaneo base:** 16 de junio de 2026  
**Fecha del plan:** 02 de julio de 2026  
**Stack:** Spring Boot 3.2.5 + Java 21 · Angular 17+

---

## Resumen de Vulnerabilidades a Mitigar

| # | Vulnerabilidad | Nivel | Herramienta | Fase |
|---|---|:---:|---|:---:|
| 1 | CSP no configurada en frontend | 🟠 Medio | ZAP | 1 |
| 2 | Cross-Domain misconfiguration (CORS) | 🟠 Medio | ZAP | 1 |
| 3 | Falta de SRI en recursos CDN | 🟠 Medio | ZAP | 1 |
| 4 | Falta cabecera X-Frame-Options | 🟠 Medio | ZAP | 1 |
| 5 | Cookie sin flag HttpOnly | 🟡 Bajo | ZAP | 1 |
| 6 | Cookie sin flag Secure | 🟡 Bajo | ZAP | 1 |
| 7 | Cookie sin atributo SameSite | 🟡 Bajo | ZAP | 1 |
| 8 | X-Content-Type-Options ausente | 🟡 Bajo | ZAP | 1 |
| 9 | HSTS no configurado | 🟡 Bajo | ZAP | 1 |
| 10 | Header X-Powered-By expuesto | 🟡 Bajo | ZAP | 2 |
| 11 | Header Server con versión expuesta | 🟡 Bajo | ZAP | 2 |
| 12 | Header X-AspNet-Version expuesto | 🟡 Bajo | ZAP | 2 |
| 13 | Divulgación de timestamps Unix | 🟡 Bajo | ZAP | 2 |
| 14 | Cobertura de ramas 7% (global) | ⚠️ Baja | JaCoCo | 3 |
| 15 | Cobertura config 1% (SecurityConfig) | 🔴 Crítico | JaCoCo | 3 |

---

## FASE 1 — Cabeceras HTTP y Seguridad de Cookies
> Estimación: **2-3 horas** · Impacto: Elimina las 4 alertas medias + 5 bajas

### MITv1: Cabeceras de seguridad en SecurityConfig.java

**Archivo:** [SecurityConfig.java](file:///e:/Personal/Estudios/UPN/Carreras%20universitarias/Ingeniería%20de%20Sistemas/Ciclos/Ciclo%2008%20-%202026%20-%20I/Hotel-Reservas-UPN-2/backend/src/main/java/com/luxury/hotel/config/SecurityConfig.java)

**Problema detectado:** ZAP reporta que faltan `X-Frame-Options`, `X-Content-Type-Options` y `Strict-Transport-Security`. El CSP actual en SecurityConfig aplica solo a respuestas del backend (API). El frontend también necesita sus propias cabeceras.

**Solución — actualizar el bloque `.headers()`:**

```java
// SecurityConfig.java — reemplazar bloque .headers() existente
.headers(headers -> headers
    // Anti-Clickjacking: ya existe, se mantiene
    .frameOptions(frameOptions -> frameOptions.deny())

    // X-Content-Type-Options: nosniff (NUEVA)
    .contentTypeOptions(Customizer.withDefaults())

    // HSTS — solo activo en producción con HTTPS (NUEVA)
    .httpStrictTransportSecurity(hsts -> hsts
        .includeSubDomains(true)
        .maxAgeInSeconds(31536000)
        .preload(true)
    )

    // CSP del backend API — ajustar para no incluir fuentes de frontend
    .contentSecurityPolicy(csp -> csp
        .policyDirectives(
            "default-src 'self'; " +
            "frame-ancestors 'none'; " +
            "object-src 'none'; " +
            "base-uri 'self';"
        )
    )
)
```

> [!IMPORTANT]
> El HSTS solo debe activarse cuando la aplicación esté desplegada con HTTPS real. En localhost sin TLS causará que el navegador rechace futuras conexiones HTTP. Para desactivarlo en desarrollo, usar un perfil condicional:

```java
// application-dev.properties
server.servlet.session.cookie.secure=false

// application-prod.properties  
server.servlet.session.cookie.secure=true
server.servlet.session.cookie.same-site=strict
```

---

### MITv2: Atributos de cookies en application.properties

**Archivo:** `backend/src/main/resources/application.properties`

**Problema detectado:** ZAP detecta cookies sin `HttpOnly`, `Secure` y `SameSite`.

**Solución — agregar al archivo de propiedades:**

```properties
# ============================================
# SEGURIDAD DE SESIÓN Y COOKIES
# ============================================
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.secure=false
# En producción cambiar a: true
server.servlet.session.cookie.same-site=strict
server.servlet.session.cookie.name=HOTELRESRV_SESSION
```

> [!NOTE]
> Dado que la autenticación es stateless (JWT Bearer token), estas cookies afectan solo las cookies de sesión auxiliares del servidor embebido. El JWT en sí se transmite por header `Authorization`, lo cual ya es seguro por diseño.

---

### MITv3: CORS reforzado en SecurityConfig.java

**Archivo:** [SecurityConfig.java](file:///e:/Personal/Estudios/UPN/Carreras%20universitarias/Ingeniería%20de%20Sistemas/Ciclos/Ciclo%2008%20-%202026%20-%20I/Hotel-Reservas-UPN-2/backend/src/main/java/com/luxury/hotel/config/SecurityConfig.java)

**Problema detectado:** ZAP plugin `10098` — "Configuración Incorrecta Cross-Domain". Aunque ya se tiene `localhost:4200`, la configuración actual expone el encabezado `Access-Control-Allow-Credentials: true` sin restricción de métodos HTTP seguros.

**Solución — actualizar `corsConfigurationSource()`:**

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // Orígenes permitidos explícitos (sin wildcard)
    configuration.setAllowedOrigins(Arrays.asList(
        "http://localhost:4200"
        // Agregar URL de producción cuando se despliegue:
        // "https://hotel-luxury.tudominio.com"
    ));

    // Métodos permitidos — eliminar PATCH si no se usa
    configuration.setAllowedMethods(Arrays.asList(
        "GET", "POST", "PUT", "DELETE", "OPTIONS"
    ));

    // Cabeceras permitidas — explícitas, no wildcard
    configuration.setAllowedHeaders(Arrays.asList(
        "Authorization",
        "Content-Type",
        "X-Requested-With"
    ));

    // Cabeceras expuestas al cliente
    configuration.setExposedHeaders(Arrays.asList(
        "Authorization"
    ));

    configuration.setAllowCredentials(true);

    // Tiempo de pre-flight cache (1 hora)
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

---

### MITv4: Subresource Integrity (SRI) en index.html

**Archivo:** [index.html](file:///e:/Personal/Estudios/UPN/Carreras%20universitarias/Ingeniería%20de%20Sistemas/Ciclos/Ciclo%2008%20-%202026%20-%20I/Hotel-Reservas-UPN-2/frontend/src/index.html)

**Problema detectado:** ZAP plugin `90003` — la fuente de Google Fonts se carga sin verificar integridad.

**Situación actual:**
```html
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
```

**Solución:**
```html
<!-- Opción A (recomendada): Usar @font-face local con fuentes descargadas -->
<!-- Descargar Inter de https://fonts.google.com/specimen/Inter y alojar en /assets/fonts/ -->
<style>
  @font-face {
    font-family: 'Inter';
    src: url('/assets/fonts/inter-variable.woff2') format('woff2');
    font-weight: 100 900;
    font-display: swap;
  }
</style>

<!-- Opción B: Mantener CDN con SRI (generar hash con: https://www.srihash.org/) -->
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap"
      rel="stylesheet"
      crossorigin="anonymous">
```

> [!NOTE]
> Google Fonts genera URLs dinámicas según el User-Agent, lo que dificulta el hash SRI estático. **La Opción A (fuentes locales)** es la más robusta y además mejora el Performance de Lighthouse al eliminar el round-trip al CDN externo.

---

## FASE 2 — Eliminación de Information Disclosure en Headers
> Estimación: **30-60 minutos** · Impacto: Elimina 4 alertas bajas

### MITv5: Suprimir X-Powered-By y Server

**Problema detectado:** ZAP plugins `10037` y `10036` — el servidor expone su tecnología e información de versión.

**Solución — `application.properties`:**
```properties
# ============================================
# OCULTAR INFORMACIÓN DEL SERVIDOR
# ============================================
server.error.include-stacktrace=never
server.error.include-message=never
server.error.include-binding-errors=never

# Para Tomcat embebido — eliminar Server header
server.servlet.encoding.charset=UTF-8
```

**Solución complementaria — agregar bean en `ApplicationConfig.java`:**
```java
import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

// Agregar dentro de ApplicationConfig:
@Bean
public WebServerFactoryCustomizer<TomcatServletWebServerFactory> serverCustomizer() {
    return factory -> factory.addConnectorCustomizers(connector -> {
        connector.setXpoweredBy(false);
    });
}
```

> [!NOTE]
> El header `X-AspNet-Version` (plugin `10061`) es específico de IIS / ASP.NET y no aplica directamente a Spring Boot. Si aparece, proviene de un proxy reverso (Nginx, IIS ARR) en el entorno de prueba. En producción, verificar la configuración del proxy.

---

### MITv6: Controlar divulgación de timestamps

**Problema detectado:** ZAP plugin `10096` — respuestas JSON incluyen timestamps Unix en los cuerpos de respuesta.

**Revisión del serializador Jackson en `application.properties`:**
```properties
# ============================================
# JACKSON — SERIALIZACIÓN DE FECHAS
# ============================================
# Usar formato ISO-8601 en lugar de timestamps Unix
spring.jackson.serialization.write-dates-as-timestamps=false
spring.jackson.serialization.write-date-timestamps-as-nanoseconds=false
spring.jackson.deserialization.fail-on-unknown-properties=false
spring.jackson.default-property-inclusion=non_null
```

---

## FASE 3 — Mejora de Cobertura de Tests (JaCoCo)
> Estimación: **4-6 horas** · Impacto: Sube cobertura de ramas de 7% → objetivo 60%+

### MITv7: Test unitario para JwtService

**Archivo a crear:** `backend/src/test/java/com/luxury/hotel/config/JwtServiceTest.java`

```java
package com.luxury.hotel.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.assertj.core.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    // Secret Base64 de 256 bits para pruebas
    private static final String TEST_SECRET =
        "dGVzdFNlY3JldEtleUZvclRlc3RpbmdQdXJwb3Nlc09ubHkxMjM0NTY3ODk=";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L); // 24h

        userDetails = User.builder()
            .username("admin@hotelluxury.com")
            .password("encodedPassword")
            .authorities(Collections.emptyList())
            .build();
    }

    @Test
    void generateToken_deberiaRetornarTokenNoNulo() {
        String token = jwtService.generateToken(userDetails);
        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    void extractUsername_deberiaDevolverEmailCorrecto() {
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);
        assertThat(username).isEqualTo("admin@hotelluxury.com");
    }

    @Test
    void isTokenValid_tokenValido_deberiaRetornarTrue() {
        String token = jwtService.generateToken(userDetails);
        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void isTokenValid_tokenDeOtroUsuario_deberiaRetornarFalse() {
        String token = jwtService.generateToken(userDetails);
        UserDetails otroUsuario = User.builder()
            .username("otro@hotel.com")
            .password("pass")
            .authorities(Collections.emptyList())
            .build();
        assertThat(jwtService.isTokenValid(token, otroUsuario)).isFalse();
    }

    @Test
    void generateToken_conTokenExpirado_isTokenValidDeberiaRetornarFalse() {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L); // expirado
        String tokenExpirado = jwtService.generateToken(userDetails);
        assertThat(jwtService.isTokenValid(tokenExpirado, userDetails)).isFalse();
    }
}
```

---

### MITv8: Test de integración para SecurityConfig (CORS y headers)

**Archivo a crear:** `backend/src/test/java/com/luxury/hotel/config/SecurityConfigTest.java`

```java
package com.luxury.hotel.config;

import com.luxury.hotel.dao.StaffUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StaffUserRepository staffUserRepository;

    @Test
    void endpoint_publico_auth_deberia_permitirAcceso() throws Exception {
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@test.com\",\"password\":\"pass\"}"))
            .andExpect(status().isNot(status().isForbidden().getMatcher()));
    }

    @Test
    void endpoint_protegido_sinToken_deberia_retornar401() throws Exception {
        mockMvc.perform(get("/reservations"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void response_deberia_incluir_header_xContentTypeOptions() throws Exception {
        mockMvc.perform(get("/auth/login"))
            .andExpect(header().string("X-Content-Type-Options", "nosniff"));
    }

    @Test
    void response_deberia_incluir_header_xFrameOptions() throws Exception {
        mockMvc.perform(get("/auth/login"))
            .andExpect(header().string("X-Frame-Options", "DENY"));
    }

    @Test
    void cors_origenPermitido_deberia_retornar_corsHeaders() throws Exception {
        mockMvc.perform(options("/auth/login")
                .header("Origin", "http://localhost:4200")
                .header("Access-Control-Request-Method", "POST"))
            .andExpect(header().exists("Access-Control-Allow-Origin"));
    }

    @Test
    void cors_origenNoPermitido_noDeberia_retornar_corsHeaders() throws Exception {
        mockMvc.perform(options("/auth/login")
                .header("Origin", "http://sitio-malicioso.com")
                .header("Access-Control-Request-Method", "POST"))
            .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }
}
```

---

### MITv9: Tests básicos para entidades del modelo

**Archivo a crear:** `backend/src/test/java/com/luxury/hotel/model/ReservationTest.java`

```java
package com.luxury.hotel.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;

class ReservationTest {

    @Test
    void reservation_constructor_deberia_asignarCampos() {
        Reservation r = new Reservation();
        r.setCheckIn(LocalDate.of(2026, 7, 10));
        r.setCheckOut(LocalDate.of(2026, 7, 15));
        r.setStatus(ReservationStatus.PENDING);

        assertThat(r.getCheckIn()).isEqualTo(LocalDate.of(2026, 7, 10));
        assertThat(r.getCheckOut()).isEqualTo(LocalDate.of(2026, 7, 15));
        assertThat(r.getStatus()).isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    void reservation_checkOut_deberia_ser_despues_de_checkIn() {
        LocalDate checkIn = LocalDate.of(2026, 7, 10);
        LocalDate checkOut = LocalDate.of(2026, 7, 15);
        assertThat(checkOut).isAfter(checkIn);
    }
}
```

---

## Criterios de Aceptación

| Mitigación | Criterio Verificable |
|---|---|
| MITv1 (Cabeceras) | ZAP no reporta plugin `10038`, `10020`, `10021`, `10035` |
| MITv2 (Cookies) | ZAP no reporta plugin `10010`, `10011`, `10054` |
| MITv3 (CORS) | ZAP no reporta plugin `10098` |
| MITv4 (SRI) | ZAP no reporta plugin `90003` |
| MITv5 (Server headers) | ZAP no reporta plugin `10037`, `10036`, `10061` |
| MITv6 (Timestamps) | ZAP no reporta plugin `10096` |
| MITv7 + MITv8 (Tests config) | JaCoCo `com.luxury.hotel.config` ≥ **60%** instrucciones |
| MITv9 (Tests model) | JaCoCo `com.luxury.hotel.model` ≥ **50%** instrucciones |
| Global JaCoCo | Cobertura de ramas global ≥ **40%** |

---

## Orden de Implementación Recomendado

```
1. application.properties  ← más rápido, sin recompilar lógica
   (cookies, Jackson timestamps, server info)

2. SecurityConfig.java     ← cabeceras HTTP + CORS reforzado

3. ApplicationConfig.java  ← ocultar header Server en Tomcat

4. index.html              ← fuentes locales (SRI)

5. Tests JwtService, SecurityConfig, Reservation
```

---

*Plan creado: 2026-07-02 · Basado en escaneo ZAP 16-Jun-2026 y reporte JaCoCo v3.0.0*
