package com.upn.hotelreservas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyOccupancyDTO {
    private String month;
    private double rate;
}
