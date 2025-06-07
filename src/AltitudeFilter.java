public class AltitudeFilter extends MeasurementFilter {

    @Override
    protected int targetId() {
        return 2;
    }

    @Override
    protected double transform(double feet) {
        return feet * 0.3048; // to meters
    }

    @Override
    protected String filterName() {
        return "Altitude";
    }
}