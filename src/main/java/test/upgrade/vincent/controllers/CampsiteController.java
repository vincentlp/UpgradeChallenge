package test.upgrade.vincent.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import test.upgrade.vincent.campsites.CampsiteService;
import test.upgrade.vincent.campsites.models.Campsite;

@RestController
@RequestMapping("/campsite")
public class CampsiteController {

    private CampsiteService campsiteService;

    public CampsiteController(@Autowired CampsiteService campsiteService) {
        this.campsiteService = campsiteService;
    }

    @GetMapping("/{id}")
    public Campsite getCampsite(@PathVariable String id) {
        return this.campsiteService.getCampsite(id);
    }
    @GetMapping("/")
    public Collection<Campsite> getAllCampsites() {
        return this.campsiteService.getAllCampsites();
    }
}
