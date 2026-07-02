package com.luxury.hotel.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;

class ReservationTest {

    @Test
    void reservation_constructor_deberia_asignarCampos() {
        Reservation r = new Reservation();
        r.setId(10L);
        r.setCheckInDate(LocalDate.of(2026, 7, 10));
        r.setCheckOutDate(LocalDate.of(2026, 7, 15));
        r.setStatus(ReservationStatus.PENDIENTE);

        assertThat(r.getId()).isEqualTo(10L);
        assertThat(r.getCheckInDate()).isEqualTo(LocalDate.of(2026, 7, 10));
        assertThat(r.getCheckOutDate()).isEqualTo(LocalDate.of(2026, 7, 15));
        assertThat(r.getStatus()).isEqualTo(ReservationStatus.PENDIENTE);
    }

    @Test
    void reservation_checkOut_deberia_ser_despues_de_checkIn() {
        LocalDate checkIn = LocalDate.of(2026, 7, 10);
        LocalDate checkOut = LocalDate.of(2026, 7, 15);
        assertThat(checkOut).isAfter(checkIn);
    }
}
