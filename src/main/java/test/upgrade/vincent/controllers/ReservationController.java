package test.upgrade.vincent.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import test.upgrade.vincent.controllers.models.CancelReservationDto;
import test.upgrade.vincent.controllers.models.CreateReservationDto;
import test.upgrade.vincent.controllers.models.UpdateReservationDto;
import test.upgrade.vincent.reservations.ReservationService;
import test.upgrade.vincent.reservations.models.Reservation;
import test.upgrade.vincent.reservations.models.ReservationAction;
import test.upgrade.vincent.workers.ReservationActionService;


@RestController
@Slf4j
@RequestMapping("/reservation")
@Api(value = "ReservationController")
public class ReservationController {

    private ReservationService reservationService;
    private ReservationActionService actionService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ReservationController(@Autowired ReservationService reservationService, @Autowired ReservationActionService actionService) {
        this.reservationService = reservationService;
        this.actionService = actionService;
    }

    @ApiOperation(value = "Get the reservation by its ID", response = Reservation.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "NOT FOUND"),
    })
    @GetMapping("/{id}")
    public ResponseEntity getReservationById(@PathVariable Long id) {
        if (this.reservationService.getReservationById(id).isEmpty())
            return new ResponseEntity("No reservation found for this ID", HttpStatus.NOT_FOUND);
        return new ResponseEntity(this.reservationService.getReservationById(id).get(), HttpStatus.OK);
    }

    @ApiOperation(value = "Get the reservation by its date", response = Reservation.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "NOT FOUND"),
    })
    @GetMapping("/")
    public ResponseEntity getReservationByDate(@RequestParam String valueDate) {
        if (this.reservationService.getReservationByDate(LocalDate.parse(valueDate, this.formatter)).isEmpty())
            return new ResponseEntity("No reservation found on this date", HttpStatus.NOT_FOUND);
        return new ResponseEntity(this.reservationService.getReservationByDate(LocalDate.parse(valueDate, this.formatter)).get(), HttpStatus.OK);

    }

    @PostMapping("/")
    public ResponseEntity createReservation(@RequestBody CreateReservationDto reservationDto) {
        try {
            Reservation reservationToCreate = Reservation.builder()
                    .campsiteId(reservationDto.getCampsiteId())
                    .userName(reservationDto.getName())
                    .userEmail(reservationDto.getEmail())
                    .startDate(LocalDate.parse(reservationDto.getArrivalDate(), this.formatter))
                    .endDate(LocalDate.parse(reservationDto.getDepartureDate(), this.formatter))
                    .build();

            if (reservationToCreate.getStartDate().isBefore(LocalDate.now()))
                return new ResponseEntity("The reservation starts in the past, impossible to create", HttpStatus.BAD_REQUEST);

            if (reservationToCreate.getEndDate().isBefore(LocalDate.now()))
                return new ResponseEntity("The reservation ends in the past, impossible to create", HttpStatus.BAD_REQUEST);

            if (reservationToCreate.getStartDate().isAfter(reservationToCreate.getEndDate()))
                return new ResponseEntity("The reservation arrival date is after the departure date, impossible to create", HttpStatus.BAD_REQUEST);

            return new ResponseEntity(this.actionService.performAction(reservationToCreate, ReservationAction.CREATE), HttpStatus.CREATED);
        } catch (Exception e) {
            String reason = String.format("Failed during creation of new reservation for user: %s, reason: %s", reservationDto.getName(), e.getMessage());
            log.error(reason);
            return new ResponseEntity(reason, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("/")
    public ResponseEntity updateReservation(@RequestBody UpdateReservationDto reservationDto) {
        try {
            Optional<Reservation> current = this.reservationService.getReservationById(reservationDto.getReservationId());
            if (!current.isPresent())
                return new ResponseEntity("The reservation does not exist, impossible to update", HttpStatus.BAD_REQUEST);
            if (current.get().getEndDate().compareTo(LocalDate.now()) > 0)
                return new ResponseEntity("The reservation is already terminated, impossible to update", HttpStatus.BAD_REQUEST);


            Reservation reservationToUpdate = Reservation.builder()
                    .id(reservationDto.getReservationId())
                    .startDate(LocalDate.parse(reservationDto.getArrivalDate(), this.formatter))
                    .endDate(LocalDate.parse(reservationDto.getDepartureDate(), this.formatter))
                    .build();
            return new ResponseEntity(this.actionService.performAction(reservationToUpdate, ReservationAction.UPDATE), HttpStatus.OK);
        } catch (Exception e) {
            String reason = String.format("Failed during update of reservationId: %s, reason: %s", reservationDto.getReservationId(), e.getMessage());
            log.error(reason);
            return new ResponseEntity(reason, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @DeleteMapping("/")
    public ResponseEntity cancelReservation(@RequestBody CancelReservationDto reservationDto) {
        try {
            Reservation reservationToCancel = Reservation.builder()
                    .id(reservationDto.getReservationId())
                    .build();

            if (this.reservationService.getReservationById(reservationDto.getReservationId()) == null)
                return new ResponseEntity("The reservation does not exist, impossible to cancel", HttpStatus.BAD_REQUEST);

            return new ResponseEntity<>(this.actionService.performAction(reservationToCancel, ReservationAction.CANCEL), HttpStatus.OK);
        } catch (Exception e) {
            String reason = String.format("Failed during cancellation of reservationId: %s, reason: %s", reservationDto.getReservationId(), e.getStackTrace());
            log.error(reason);
            return new ResponseEntity(reason, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
