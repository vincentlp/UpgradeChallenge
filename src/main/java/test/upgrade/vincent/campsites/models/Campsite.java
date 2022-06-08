package test.upgrade.vincent.campsites.models;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Campsite {

    private String id;
    private String name;
    private String description;

}
