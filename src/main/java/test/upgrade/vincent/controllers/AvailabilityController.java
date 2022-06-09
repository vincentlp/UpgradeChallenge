package test.upgrade.vincent.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import test.upgrade.vincent.availability.AvailabilityService;

@RestController
@RequestMapping("/availability")
public class AvailabilityController {


    private final AvailabilityService availabilityService;

    public AvailabilityController(@Autowired AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping("/{startDate}/{endDate}")
    public List<String> getAvailabilities(@PathVariable String startDate, @PathVariable String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return this.availabilityService.getAvailabilities(LocalDate.parse(startDate, formatter), LocalDate.parse(endDate, formatter))
                .stream().map(x -> x.toString())
                .collect(Collectors.toList());
    }

}
