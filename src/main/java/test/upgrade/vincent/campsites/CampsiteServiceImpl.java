package test.upgrade.vincent.campsites;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import test.upgrade.vincent.campsites.models.Campsite;

@Service
public class CampsiteServiceImpl implements CampsiteService {

    private Campsite uniqueCampsite;

    public CampsiteServiceImpl() {
        this.uniqueCampsite = Campsite.builder()
                .id("UNIQUE_CAMPSITE_ID")
                .name("Unique Campsite")
                .description("For demo purpose, this is a unique Campsite")
                .build();
    }

    @Override
    public Campsite getCampsite(String id) {
        if (uniqueCampsite.getId().equals(id)) {
            return this.uniqueCampsite;
        } else {
            return null;
        }
    }

    @Override
    public List<Campsite> getAllCampsites() {
        return Collections.singletonList(this.uniqueCampsite);
    }
}
