package ng.dat.ar.model;

import android.location.Location;

import java.text.DecimalFormat;

/**
 * Created by ntdat on 1/16/17.
 */

public class ARPoint {
    Location location;
    String name;
    String description;

    public ARPoint(String name, String description, double lat, double lon, double altitude) {
        this.name = name;
        this.description = description;
        location = new Location("ARPoint");
        location.setLatitude(lat);
        location.setLongitude(lon);
        location.setAltitude(altitude);
    }

    public Location getLocation() {
        return location;
    }

    public String getName() { return name; }

    public String getDescription() {
        return description;
    }

}
