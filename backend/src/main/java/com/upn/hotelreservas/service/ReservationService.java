package com.upn.hotelreservas.service;

import com.upn.hotelreservas.dao.ReservationRepository;
import com.upn.hotelreservas.model.Reservation;
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
        return repository.save(reservation);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
