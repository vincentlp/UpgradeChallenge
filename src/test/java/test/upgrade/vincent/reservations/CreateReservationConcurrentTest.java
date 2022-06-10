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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import test.upgrade.vincent.UtilsTest;
import test.upgrade.vincent.controllers.ReservationController;
import test.upgrade.vincent.controllers.models.CancelReservationDto;
import test.upgrade.vincent.reservations.models.Reservation;
import test.upgrade.vincent.reservations.models.ReservationAction;
import test.upgrade.vincent.reservations.repositories.ReservationRepository;
import test.upgrade.vincent.workers.ReservationActionService;

@SpringBootTest
public class CreateReservationConcurrentTest {


    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationActionService actionService;

    @Autowired
    private ReservationController reservationController;

    private Reservation defaultReservation;

    @BeforeEach
    public void beforeEach() {
        this.reservationRepository.deleteAll();
    }


    @Test
    public void with_multithread_on_same_range_date_create_unique_reservation() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(10);

        IntStream.range(0, 1000)
                .forEach(count -> service.submit(this::simulateNewReservation));

        service.awaitTermination(1000, TimeUnit.MILLISECONDS);

        List<Reservation> allReservationCreated = this.reservationRepository.findAll();
        assertThat(allReservationCreated.size()).isEqualTo(1);
        this.reservationController.cancelReservation(new CancelReservationDto(allReservationCreated.get(0).getId()));
    }

    @Test
    public void with_multithread_on_same_range_date_update_unique_reservation() throws InterruptedException {

        defaultReservation = (Reservation) this.reservationController.createReservation(UtilsTest.buildCreateDto(12L, 14L)).getBody();
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

    private Runnable simulateUpdateReservation() {
        defaultReservation.setStartDate(defaultReservation.getStartDate().plusDays(7));
        defaultReservation.setEndDate(defaultReservation.getEndDate().plusDays(7));
        try {
            this.actionService.performAction(defaultReservation, ReservationAction.UPDATE);
        } catch (Exception e) {

        }
        return null;
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
}
