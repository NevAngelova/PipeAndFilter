public class Plumber
{
    public static void main( String argv[])
    {
        /****************************************************************************
         * Here we instantiate three filters.
         ****************************************************************************/

        SourceFilter sourceFilter = new SourceFilter();

        // Middle filers
        TemperatureFilter temperatureFilter = new TemperatureFilter();
        AltitudeFilter altitudeFilter = new AltitudeFilter();

        SinkFilter sinkFilter = new SinkFilter();

        /****************************************************************************
         * Here we connect the filters starting with the sink filter which
         * we connect to the middle filters. Then we connect the middle filters to the
         * source filter (sinkFilter).
         ****************************************************************************/

        sinkFilter.Connect(altitudeFilter); // This esstially says, "connect sinkFilter input port to Filter2 output port
        altitudeFilter.Connect(temperatureFilter);
        temperatureFilter.Connect(sourceFilter); // This esstially says, "connect Filter2 intput port to sourceFilter output port

        /****************************************************************************
         * Here we start the filters up. All-in-all,... its really kind of boring.
         ****************************************************************************/

        sourceFilter.start();
        temperatureFilter.start();
        altitudeFilter.start();
        sinkFilter.start();

    } // main

} // Plumber