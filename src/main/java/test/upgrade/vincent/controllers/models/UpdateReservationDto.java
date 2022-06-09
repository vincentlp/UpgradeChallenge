package test.upgrade.vincent.controllers.models;

import lombok.Data;

@Data
public class UpdateReservationDto {
    private Long reservationId;
    private String arrivalDate;
    private String departureDate;
}
