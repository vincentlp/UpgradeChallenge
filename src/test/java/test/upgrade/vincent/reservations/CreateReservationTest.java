package test.upgrade.vincent.reservations;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import test.upgrade.vincent.UtilsTest;
import test.upgrade.vincent.controllers.ReservationController;
import test.upgrade.vincent.controllers.models.CreateReservationDto;
import test.upgrade.vincent.reservations.models.Reservation;
import test.upgrade.vincent.reservations.models.ReservationAction;
import test.upgrade.vincent.reservations.repositories.ReservationRepository;
import test.upgrade.vincent.workers.ReservationActionService;


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CreateReservationTest {

    @Autowired
    private ReservationController reservationController;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationActionService actionService;

    private Reservation defaultReservation;

    @BeforeEach
    public void beforeEach() {
        this.reservationRepository.deleteAll();
    }

    @Test
    public void when_date_available_create_new_reservation() {
        CreateReservationDto createDto = UtilsTest.buildCreateDto(1L, 3L);
        ResponseEntity response = this.reservationController.createReservation(createDto);

        assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(((Reservation) response.getBody()).getId()).isNotNull();
    }

    @Test
    public void when_date_not_available_return_error() {
        CreateReservationDto createDto = UtilsTest.buildCreateDto(1L, 3L);
        this.reservationController.createReservation(createDto);
        ResponseEntity response = this.reservationController.createReservation(createDto);

        assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().toString()).contains("date is not available");

    }

    @Test
    public void when_date_too_far_in_future_return_error() {
        CreateReservationDto createDto = UtilsTest.buildCreateDto(1000L, 10001L);
        ResponseEntity response = this.reservationController.createReservation(createDto);

        assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().toString()).contains("date is not valid");

    }

    @Test
    public void when_date_range_too_long_return_error() {
        CreateReservationDto createDto = UtilsTest.buildCreateDto(1L, 1000L);
        ResponseEntity response = this.reservationController.createReservation(createDto);

        assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().toString()).contains("date is not valid");

    }

    @Test
    public void when_date_range_in_the_past_return_error() {
        CreateReservationDto createDto = UtilsTest.buildCreateDto(-1L, 0L);
        ResponseEntity response = this.reservationController.createReservation(createDto);

        assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().toString()).contains("in the past");

    }

    @Test
    public void with_multithread_on_same_range_date_create_unique_reservation() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(10);

        IntStream.range(0, 1000)
                .forEach(count -> service.submit(this::simulateNewReservation));

        service.awaitTermination(1000, TimeUnit.MILLISECONDS);

        List<Reservation> allReservationCreated = this.reservationRepository.findAll();
        assertThat(allReservationCreated.size()).isEqualTo(1);

    }

    @Test
    public void with_multithread_on_same_range_date_update_unique_reservation() throws InterruptedException {

        defaultReservation = (Reservation) this.reservationController.createReservation(UtilsTest.buildCreateDto(2L, 4L)).getBody();
        ExecutorService service = Executors.newFixedThreadPool(10);

        IntStream.range(0, 1000)
                .forEach(count -> {
                    service.submit(this::simulateUpdateReservation);
                    if (count == 500) service.submit(this::simulateNewReservation);
                });

        service.awaitTermination(1000, TimeUnit.MILLISECONDS);

        List<Reservation> allReservationCreated = this.reservationRepository.findAll();
        assertThat(allReservationCreated.size()).isEqualTo(2);

    }

    private Runnable simulateNewReservation() {
        LocalDate arrivalDate = LocalDate.now().plusDays(2);
        LocalDate departureDate = LocalDate.now().plusDays(4);
        try {
            this.actionService.performAction(UtilsTest.buildReservation(arrivalDate, departureDate), ReservationAction.CREATE);
        } catch (Exception e) {

        }
        return null;
    }

    private Runnable simulateUpdateReservation() {
        defaultReservation.setStartDate(defaultReservation.getStartDate().plusDays(7));
        defaultReservation.setEndDate(defaultReservation.getEndDate().plusDays(7));
        try {
            this.actionService.performAction(defaultReservation, ReservationAction.UPDATE);
        } catch (Exception e) {

        }
        return null;
    }

}
