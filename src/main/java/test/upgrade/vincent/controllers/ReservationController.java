package test.upgrade.vincent.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import test.upgrade.vincent.controllers.models.CancelReservationDto;
import test.upgrade.vincent.controllers.models.CreateReservationDto;
import test.upgrade.vincent.controllers.models.UpdateReservationDto;
import test.upgrade.vincent.reservations.ReservationService;
import test.upgrade.vincent.reservations.models.Reservation;
import test.upgrade.vincent.reservations.models.ReservationAction;
import test.upgrade.vincent.workers.ReservationActionService;


@RestController
@RequestMapping("/reservation")
public class ReservationController {

    private ReservationService reservationService;
    private ReservationActionService actionService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ReservationController(@Autowired ReservationService reservationService, @Autowired ReservationActionService actionService) {
        this.reservationService = reservationService;
        this.actionService = actionService;
    }

    @GetMapping("/{id}")
    public Reservation getReservationById(@PathVariable Long id) {
        return this.reservationService.getReservationById(id);
    }

    @GetMapping("/")
    public Reservation getReservationByDate(@RequestParam String valueDate) {
        return this.reservationService.getReservationByDate(LocalDate.parse(valueDate, this.formatter));
    }

    @PostMapping("/")
    Reservation createReservation(@RequestBody CreateReservationDto reservationDto) {
        Reservation reservationToCreate = Reservation.builder()
                .campsiteId(reservationDto.getCampsiteId())
                .userName(reservationDto.getName())
                .userEmail(reservationDto.getEmail())
                .startDate(LocalDate.parse(reservationDto.getArrivalDate(), this.formatter))
                .endDate(LocalDate.parse(reservationDto.getDepartureDate(), this.formatter))
                .build();
        return this.actionService.performAction(reservationToCreate, ReservationAction.CREATE);
    }

    @PutMapping("/")
    Reservation updateReservation(@RequestBody UpdateReservationDto reservationDto) {
        Reservation reservationToUpdate = Reservation.builder()
                .id(reservationDto.getReservationId())
                .startDate(LocalDate.parse(reservationDto.getArrivalDate(), this.formatter))
                .endDate(LocalDate.parse(reservationDto.getDepartureDate(), this.formatter))
                .build();
        return this.actionService.performAction(reservationToUpdate, ReservationAction.UPDATE);
    }

    @DeleteMapping("/")
    Reservation cancelReservation(@RequestBody CancelReservationDto reservationDto) {
        Reservation reservationToCancel = Reservation.builder()
                .id(reservationDto.getReservationId())
                .build();

        return this.actionService.performAction(reservationToCancel, ReservationAction.CANCEL);
    }

}
