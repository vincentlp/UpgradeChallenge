package test.upgrade.vincent;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import test.upgrade.vincent.controllers.models.CreateReservationDto;

public final class UtilsTest {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static CreateReservationDto buildCreateDto(Long offsetArrival, Long offsetDeparture) {
        return CreateReservationDto.builder()
                .campsiteId("unusedCampsiteId")
                .name("user1")
                .email("user1@gmail.com")
                .arrivalDate(LocalDate.now().plusDays(offsetArrival).toString())
                .departureDate(LocalDate.now().plusDays(offsetDeparture).toString())
                .build();
    }

}
