package com.luxury.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportStatsDTO {
    private double occupancyRate;
    private double monthlyRevenue;
    private int totalReservations;
    private int totalRooms;
    private int cancellations;
    private List<MonthlyOccupancyDTO> monthlyOccupancy;
    private List<ServiceUsageDTO> topServices;
}
