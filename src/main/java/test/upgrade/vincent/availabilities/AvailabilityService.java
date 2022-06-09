package test.upgrade.vincent.availabilities;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityService {

    List<LocalDate> getAvailabilities(LocalDate startDate, LocalDate endDate);

}
