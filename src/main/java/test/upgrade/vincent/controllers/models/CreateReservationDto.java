package test.upgrade.vincent.controllers.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateReservationDto {
    private String campsiteId;
    private String email;
    private String name;
    private String arrivalDate;
    private String departureDate;
}
