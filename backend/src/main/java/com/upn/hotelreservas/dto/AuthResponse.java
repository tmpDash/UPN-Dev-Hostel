package com.upn.hotelreservas.dto;

import com.upn.hotelreservas.model.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private Long id;
    private String email;
    private String fullName;
    private UserRole role;
    private String token;
}
