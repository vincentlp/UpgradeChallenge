package test.upgrade.vincent.availabilities;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import test.upgrade.vincent.UtilsTest;
import test.upgrade.vincent.controllers.AvailabilityController;
import test.upgrade.vincent.controllers.ReservationController;
import test.upgrade.vincent.controllers.models.CreateReservationDto;
import test.upgrade.vincent.reservations.repositories.ReservationRepository;

@SpringBootTest
public class RetrieveAvailabilityTest {


    @Autowired
    private AvailabilityController availabilityController;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationController reservationController;

    @BeforeEach
    public void beforeEach() {
        this.reservationRepository.deleteAll();
    }

    @Test
    public void when_everything_available_return_all_dates() {
        LocalDate arrivalDate = LocalDate.now().plusDays(1L);
        LocalDate departureDate = LocalDate.now().plusDays(5L);
        int nbDays = (int) (DAYS.between(arrivalDate, departureDate) + 1);

        List<String> result = this.availabilityController.getAvailabilities(arrivalDate.toString(), departureDate.toString());

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(nbDays);

    }

    @Test
    public void when_nothing_available_return_empty_dates() {
        CreateReservationDto createDto = UtilsTest.buildCreateDto(1L, 3L);
        this.reservationController.createReservation(createDto);

        LocalDate arrivalDate = LocalDate.now().plusDays(1L);
        LocalDate departureDate = LocalDate.now().plusDays(3L);

        List<String> result = this.availabilityController.getAvailabilities(arrivalDate.toString(), departureDate.toString());

        assertThat(result).isEmpty();

    }

    @Test
    public void when_some_available_return_partial_dates() {
        CreateReservationDto createDto = UtilsTest.buildCreateDto(10L, 11L);
        this.reservationController.createReservation(createDto);

        LocalDate arrivalDate = LocalDate.now();
        LocalDate departureDate = LocalDate.now().plusMonths(1L);
        int nbDays = (int) (DAYS.between(arrivalDate, departureDate) + 1);

        List<String> result = this.availabilityController.getAvailabilities(arrivalDate.toString(), departureDate.toString());

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isLessThan(nbDays);

    }

}
