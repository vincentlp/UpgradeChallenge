package test.upgrade.vincent.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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


@RestController
@RequestMapping("/reservation")
public class ReservationController {

    private ReservationService reservationService;

    public ReservationController(@Autowired ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/{id}")
    public Reservation getReservationById(@PathVariable Long id) {
        return this.reservationService.getReservationById(id);
    }

    @GetMapping("/")
    public Reservation getReservationByDate(@RequestParam String valueDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return this.reservationService.getReservationByDate(LocalDate.parse(valueDate, formatter));
    }

    @PostMapping("/")
    Reservation createReservation(@RequestBody CreateReservationDto reservationDto) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @PutMapping("/")
    Reservation updateReservation(@RequestBody UpdateReservationDto reservationDto) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @DeleteMapping("/")
    ResponseEntity<Reservation> cancelReservation(@RequestBody CancelReservationDto reservationDto) {
        throw new UnsupportedOperationException("not implemented yet");
    }

}
