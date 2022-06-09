package test.upgrade.vincent.reservations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import test.upgrade.vincent.reservations.models.Reservation;
import test.upgrade.vincent.reservations.repositories.ReservationRepository;

@Service
public class ReservationServiceImpl implements ReservationService {

    private Map<Long, Reservation> reservationCache = Collections.emptyMap();
    private final ReservationRepository reservationRepository;

    public ReservationServiceImpl(@Autowired ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public Reservation getReservationById(Long id) {
        if (this.reservationCache.isEmpty()) this.reloadCache();
        return this.reservationCache.getOrDefault(id, null);
    }

    @Override
    public Reservation getReservationByDate(LocalDate valueDate) {
        if (this.reservationCache.isEmpty()) this.reloadCache();
        Optional<Reservation> optionalReservation = this.reservationCache.values().stream()
                .filter(x -> x.getStartDate().compareTo(valueDate) <= 0)
                .filter(x -> x.getEndDate().compareTo(valueDate) >= 0)
                .findFirst();
        return optionalReservation.orElse(null);
    }

    @Override
    public Reservation addReservation(Reservation reservation) {
        Reservation inserted = this.reservationRepository.save(reservation);
        this.reservationCache.putIfAbsent(inserted.getId(), inserted);
        return inserted;
    }

    @Override
    public Reservation updateReservation(Reservation reservation) {
        Reservation current = this.reservationCache.get(reservation.getId());
        Reservation update = Reservation.builder()
                .id(current.getId())
                .campsiteId(current.getCampsiteId())
                .userName(current.getUserName())
                .userEmail(current.getUserEmail())
                .startDate(reservation.getStartDate())
                .endDate(reservation.getEndDate())
                .createdOn(current.getCreatedOn())
                .updatedOn(LocalDate.now())
                .build();
        this.reservationRepository.save(update);
        this.reservationCache.put(current.getId(), update);
        return update;
    }

    @Override
    public Reservation cancelReservation(Reservation reservation) {
        this.reservationRepository.delete(reservation);
        this.reservationCache.remove(reservation.getId());
        return reservation;
    }

    @PostConstruct
    private void reloadCache() {
        LocalDate today = LocalDate.now();
        List<Reservation> reservationsAlive = this.reservationRepository.findAllByEndDate(today);
        this.reservationCache = reservationsAlive.stream()
                .collect(Collectors.toMap(Reservation::getId, Function.identity()));
    }
}
