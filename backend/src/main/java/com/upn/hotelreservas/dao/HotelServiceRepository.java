package com.upn.hotelreservas.dao;

import com.upn.hotelreservas.model.HotelServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelServiceRepository extends JpaRepository<HotelServiceEntity, Long> {
}
