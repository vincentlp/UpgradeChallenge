package test.upgrade.vincent;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import test.upgrade.vincent.controllers.models.CreateReservationDto;
import test.upgrade.vincent.reservations.models.Reservation;

public final class UtilsTest {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static CreateReservationDto buildCreateDto(Long offsetArrival, Long offsetDeparture) {
        return CreateReservationDto.builder()
                .campsiteId("unusedCampsiteId")
                .name("user")
                .email("user@gmail.com")
                .arrivalDate(LocalDate.now().plusDays(offsetArrival).toString())
                .departureDate(LocalDate.now().plusDays(offsetDeparture).toString())
                .build();
    }

    public static Reservation buildReservation(LocalDate arrivalDate, LocalDate departureDate){
        return Reservation.builder()
                .campsiteId("unusedCamsiteId")
                .userName("user")
                .userEmail("user@gmail.com")
                .startDate(arrivalDate)
                .endDate(departureDate)
                .build();
    }
}
