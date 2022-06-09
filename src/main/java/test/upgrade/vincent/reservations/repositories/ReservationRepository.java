package test.upgrade.vincent.reservations.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import test.upgrade.vincent.reservations.models.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query(value = "SELECT r FROM Reservation r WHERE r.endDate >= :endDate")
    List<Reservation> findAllByEndDate(@Param("endDate") LocalDate endDate);

}
