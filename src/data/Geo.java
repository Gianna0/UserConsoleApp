package data;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Geo {
    public BigDecimal lat;
    public BigDecimal lng;

    public Geo(){}

    public Geo(BigDecimal lat, BigDecimal lng) {
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "Geo{" +
                "lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                '}';
    }

    public String convertToDMSCord() {
        String latDMS = convertToDMS(lat, 1);
        String lngDMS = convertToDMS(lng, 0);
        return latDMS + " " + lngDMS;
    }

    private String convertToDMS(BigDecimal lat, int location) {
        BigDecimal latPositive = lat.abs();
        int degree = lat.intValue();
        int degreeTemp = lat.intValue();
        int minutes = (lat.subtract(BigDecimal.valueOf((double) degree))).multiply(BigDecimal.valueOf(60.0)).intValue();
        degree = degree < 0 ? degree * (-1) : degree;
        minutes = minutes < 0 ? minutes * (-1) : minutes;
        BigDecimal minutesDivided = BigDecimal.valueOf(minutes/60.0);
        BigDecimal seconds = latPositive.subtract(BigDecimal.valueOf((double) degree));
        seconds = seconds.subtract(minutesDivided);
        seconds = seconds.multiply(BigDecimal.valueOf((double) 3600));
        BigDecimal z = seconds.setScale(  0, RoundingMode.HALF_UP);
        String dms = degree + "d" + minutes + "'" + z + "\"";

        if (degreeTemp >= 0) {
            String position = location == 1 ? "N" : "W";
            dms += position;
        } else {
            String position = location == 1 ? "S" : "E";
            dms += position;
        }

        return dms;
    }
}
