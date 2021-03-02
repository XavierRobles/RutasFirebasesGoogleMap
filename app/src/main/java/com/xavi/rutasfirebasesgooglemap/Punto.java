package com.xavi.rutasfirebasesgooglemap;

import com.google.android.gms.maps.model.LatLng;

public class Punto {
    private Double lat, lng;

    public Punto() {
    }


    public Punto(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "Punto{" +
                "lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
