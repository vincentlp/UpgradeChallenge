package test.upgrade.vincent.availabilities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AvailabilityServiceImpl implements AvailabilityService {

    private final CacheableAvailability cache;

    public AvailabilityServiceImpl(@Autowired CacheableAvailability cache) {
        this.cache = cache;
    }

    @Override
    public List<LocalDate> getAvailabilities(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> availabilities = new ArrayList<>();

        for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
            if (this.cache.isAvailable(date)) availabilities.add(date);
        }
        return availabilities;
    }

}
