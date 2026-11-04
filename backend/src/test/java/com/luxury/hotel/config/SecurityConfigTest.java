package com.luxury.hotel.config;

import com.luxury.hotel.controller.AuthController;
import com.luxury.hotel.dao.StaffUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@WebMvcTest(AuthController.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private StaffUserRepository repository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsService userDetailsService;

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
    void response_deberia_incluir_header_strictTransportSecurity() throws Exception {
        // HSTS se incluye solo si la petición es segura (HTTPS)
        mockMvc.perform(get("/auth/login").secure(true))
            .andExpect(header().string("Strict-Transport-Security", "max-age=31536000 ; includeSubDomains"));
    }

    @Test
    void corsConfigurationSource_deberiaPermitirOrigenAngular() {
        SecurityConfig config = new SecurityConfig(null, null);
        ReflectionTestUtils.setField(config, "allowedOrigins",
                "http://localhost:4200, https://hotel-luxury.pages.dev");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/auth/login");

        CorsConfiguration corsConfig = config.corsConfigurationSource().getCorsConfiguration(request);

        assertThat(corsConfig).isNotNull();
        assertThat(corsConfig.getAllowedOrigins())
                .containsExactly("http://localhost:4200", "https://hotel-luxury.pages.dev");
        assertThat(corsConfig.getAllowedMethods()).contains("GET", "POST", "PUT", "DELETE", "OPTIONS");
        assertThat(corsConfig.getAllowedHeaders()).contains("Authorization", "Content-Type", "X-Requested-With");
        assertThat(corsConfig.getAllowCredentials()).isTrue();
    }
}
