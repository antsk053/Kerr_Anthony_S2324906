package org.me.gcu.kerr_anthony_s2324906;

public class RoadLocation {
    private double fromLat;
    private double fromLong;
    private double toLat;
    private double toLong;

    public RoadLocation(double fromLat, double fromLong, double toLat, double toLong) {
        this.fromLat = fromLat;
        this.fromLong = fromLong;
        this.toLat = toLat;
        this.toLong = toLong;
    }

    public double getFromLat() {
        return fromLat;
    }

    public double getToLat() {
        return toLat;
    }


    public double getFromLong() {
        return fromLong;
    }

    public double getToLong() {
        return toLong;
    }
}