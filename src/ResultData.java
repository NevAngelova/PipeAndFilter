import java.util.Date;

public class ResultData {

    private final Date time;
    private final double temperature;
    private final double altitude;

    public ResultData(Date time, double temperature, double altitude) {
        this.time = time;
        this.temperature = temperature;
        this.altitude = altitude;
    }

    public Date getTime() {
        return time;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getAltitude() {
        return altitude;
    }
}