package test.upgrade.vincent.availability;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityService {

    List<LocalDate> getAvailabilities(LocalDate startDate, LocalDate endDate);

}
