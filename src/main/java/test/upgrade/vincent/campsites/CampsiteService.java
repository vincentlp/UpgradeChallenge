package test.upgrade.vincent.campsites;

import java.util.List;

import test.upgrade.vincent.campsites.models.Campsite;

public interface CampsiteService {

    Campsite getCampsite(String id);

    List<Campsite> getAllCampsites();
}
