package com.luxury.hotel.service;

import com.luxury.hotel.dao.HotelRepository;
import com.luxury.hotel.dao.ReservationRepository;
import com.luxury.hotel.dto.MonthlyOccupancyDTO;
import com.luxury.hotel.dto.ReportStatsDTO;
import com.luxury.hotel.dto.ServiceUsageDTO;
import com.luxury.hotel.model.Hotel;
import com.luxury.hotel.model.Reservation;
import com.luxury.hotel.model.ReservationServiceDetail;
import com.luxury.hotel.model.ReservationStatus;
import org.springframework.stereotype.Service;

import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final ReservationRepository reservationRepository;
    private final HotelRepository hotelRepository;

    public ReportService(ReservationRepository reservationRepository, HotelRepository hotelRepository) {
        this.reservationRepository = reservationRepository;
        this.hotelRepository = hotelRepository;
    }

    public ReportStatsDTO getGeneralStats() {
        List<Reservation> allReservations = reservationRepository.findAll();
        List<Hotel> allHotels = hotelRepository.findAll();

        // 1. Calcular Ingresos Totales
        double totalRevenue = allReservations.stream()
                .filter(r -> r.getStatus() != ReservationStatus.CANCELADA)
                .mapToDouble(r -> r.getTotalPrice() != null ? r.getTotalPrice() : 0.0)
                .sum();

        // 2. Calcular Cancelaciones
        long cancellations = allReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CANCELADA)
                .count();

        // 3. Calcular Tasa de Ocupación
        int totalRooms = allHotels.stream().mapToInt(Hotel::getHabitaciones).sum();
        long activeReservations = allReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMADA)
                .count();
        
        double occupancyRate = totalRooms > 0 ? (activeReservations * 100.0 / totalRooms) : 0;

        // 4. Ocupación por Mes (Últimos 6 meses o meses presentes)
        Map<String, Long> occupancyByMonth = allReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMADA)
                .collect(Collectors.groupingBy(
                        r -> r.getCheckInDate().getMonth().getDisplayName(TextStyle.SHORT, new Locale("es", "ES")),
                        Collectors.counting()
                ));

        List<MonthlyOccupancyDTO> monthlyOccupancy = occupancyByMonth.entrySet().stream()
                .map(e -> new MonthlyOccupancyDTO(e.getKey(), totalRooms > 0 ? (e.getValue() * 100.0 / totalRooms) : 0))
                .collect(Collectors.toList());

        // 5. Top Servicios
        Map<String, Long> serviceUsage = allReservations.stream()
                .flatMap(r -> r.getAddedServices().stream())
                .collect(Collectors.groupingBy(
                        ReservationServiceDetail::getName,
                        Collectors.summingLong(ReservationServiceDetail::getQuantity)
                ));

        List<ServiceUsageDTO> topServices = serviceUsage.entrySet().stream()
                .map(e -> new ServiceUsageDTO(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingLong(ServiceUsageDTO::getUsageCount).reversed())
                .limit(5)
                .collect(Collectors.toList());

        return new ReportStatsDTO(
                Math.round(occupancyRate * 100.0) / 100.0,
                totalRevenue,
                allReservations.size(),
                totalRooms,
                (int) cancellations,
                monthlyOccupancy,
                topServices
        );
    }
}
