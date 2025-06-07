public class TemperatureFilter extends MeasurementFilter {
    @Override
    protected int targetId() {
        return 4;
    }

    @Override
    protected double transform(double fahrenheit) {
        return (fahrenheit - 32) * 5.0 / 9.0;
    }

    @Override
    protected String filterName() {
        return "Temperature";
    }
}
