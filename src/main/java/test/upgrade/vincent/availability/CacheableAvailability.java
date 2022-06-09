package test.upgrade.vincent.availability;

import java.time.LocalDate;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import test.upgrade.vincent.reservations.ReservationService;
import test.upgrade.vincent.reservations.models.Reservation;

@Service
@Slf4j
public class CacheableAvailability {

    private final ReservationService reservationService;

    public CacheableAvailability(@Autowired ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Cacheable(value = "availabilities", key = "#date")
    public boolean isAvailable(LocalDate date) {
        log.debug("recompute availability for valueDate: {}", date.toString());
        Reservation reservation = this.reservationService.getReservationByDate(date);
        return reservation == null;
    }

    @CacheEvict(value = "availabilities", key = "#date")
    public void evictAvailability(LocalDate date) {
        log.warn("remove date in cache: {}", date.toString());
    }
}
