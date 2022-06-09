package test.upgrade.vincent.reservations;


import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import test.upgrade.vincent.reservations.models.Reservation;
import test.upgrade.vincent.reservations.repositories.ReservationRepository;


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RetrieveReservationTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    private Reservation defaultReservation;

    @BeforeAll
    public void beforeAll() {
        this.reservationRepository.deleteAll();
        Reservation reservation1 = Reservation.builder()
                .campsiteId("unusedCampsiteId")
                .userName("user1")
                .userEmail("user1@gmail.com")
                .createdOn(LocalDate.now())
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(3))
                .build();

        defaultReservation = this.reservationRepository.save(reservation1);
    }


    @Test
    public void when_default_reservation_is_created_return_reservation() {
        Reservation reservation = reservationService.getReservationById(defaultReservation.getId());
        assertThat(reservation).isEqualTo(defaultReservation);
    }

    @Test
    public void when_retrieve_wrong_reservation_id_return_null() {
        Reservation reservation = reservationService.getReservationById(0L);
        assertThat(reservation).isNull();
    }

    @Test
    public void when_retrieve_reservation_by_arrival_date_return_reservation() {
        Reservation reservation = reservationService.getReservationByDate(defaultReservation.getStartDate());
        assertThat(reservation).isEqualTo(defaultReservation);
    }

    @Test
    public void when_retrieve_reservation_by_departure_date_return_reservation() {
        Reservation reservation = reservationService.getReservationByDate(defaultReservation.getEndDate());
        assertThat(reservation).isEqualTo(defaultReservation);
    }

    @Test
    public void when_retrieve_reservation_by_in_between_date_return_reservation() {
        Reservation reservation = reservationService.getReservationByDate(defaultReservation.getStartDate().plusDays(1));
        assertThat(reservation).isEqualTo(defaultReservation);
    }
}
