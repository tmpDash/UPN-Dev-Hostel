package com.upn.hotelreservas.service;

import com.upn.hotelreservas.dao.HotelServiceRepository;
import com.upn.hotelreservas.model.HotelServiceEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class HotelServiceService {

    private final HotelServiceRepository repository;

    public HotelServiceService(HotelServiceRepository repository) {
        this.repository = repository;
    }

    public List<HotelServiceEntity> listarTodos() {
        return repository.findAll();
    }

    public Optional<HotelServiceEntity> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public HotelServiceEntity guardar(HotelServiceEntity service) {
        return repository.save(service);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
