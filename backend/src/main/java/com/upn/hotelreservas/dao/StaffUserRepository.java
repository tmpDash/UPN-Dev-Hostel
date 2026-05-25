package com.upn.hotelreservas.dao;

import com.upn.hotelreservas.model.StaffUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface StaffUserRepository extends JpaRepository<StaffUser, Long> {
    Optional<StaffUser> findByEmail(String email);
}
