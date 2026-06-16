package com.luxury.hotel.service;

import com.luxury.hotel.dao.ReservationRepository;
import com.luxury.hotel.model.Reservation;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository repository;

    public ReservationService(ReservationRepository repository) {
        this.repository = repository;
    }

    public List<Reservation> listarTodas() {
        return repository.findAll();
    }

    public List<Reservation> listarRecientes() {
        return repository.findTop5ByOrderByIdDesc();
    }

    public Optional<Reservation> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Reservation guardar(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("La reserva no puede ser nula");
        }
        if (reservation.getCheckInDate() != null && reservation.getCheckOutDate() != null) {
            if (reservation.getCheckOutDate().isBefore(reservation.getCheckInDate()) || 
                reservation.getCheckOutDate().isEqual(reservation.getCheckInDate())) {
                throw new IllegalArgumentException("La fecha de check-out debe ser posterior al check-in");
            }
        }
        return repository.save(reservation);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
