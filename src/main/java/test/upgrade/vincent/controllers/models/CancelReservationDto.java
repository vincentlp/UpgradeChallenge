package test.upgrade.vincent.controllers.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CancelReservationDto {
    private Long reservationId;
}
