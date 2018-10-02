package ng.dat.ar.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class FirebasePoint {

    public String descripcion;
    public double latitud;
    public double longitud;
    public long altitud;

    public FirebasePoint() {
    }

    public FirebasePoint(String descripcion, double latitud, double longitud, long altitud){
        this.descripcion = descripcion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.altitud = altitud;
    }

    public String getDescripcion() { return descripcion; }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public long getAltitud() {
        return altitud;
    }

    public void setAltitud(long altitud) {
        this.altitud = altitud;
    }
}
