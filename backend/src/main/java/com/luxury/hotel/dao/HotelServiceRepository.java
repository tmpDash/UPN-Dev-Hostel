package com.luxury.hotel.dao;

import com.luxury.hotel.model.HotelServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelServiceRepository extends JpaRepository<HotelServiceEntity, Long> {
}
