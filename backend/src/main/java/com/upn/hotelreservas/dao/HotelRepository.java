package com.upn.hotelreservas.dao;

import com.upn.hotelreservas.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * DAO (Repository) for accessing Hotel entities.
 */
@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    // You can add custom query methods here, e.g.:
    // List<Hotel> findByCiudad(String ciudad);
}
