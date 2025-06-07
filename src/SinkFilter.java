import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;                        // This class is used to interpret time words
import java.text.SimpleDateFormat;        // This class is used to format and write time in a string format.

public class SinkFilter extends FilterFramework {
    public void run() {
        /************************************************************************************
         *	TimeStamp is used to compute time using java.util's Calendar class.
         * 	TimeStampFormat is used to format the time value so that it can be easily printed
         *	to the terminal.
         *************************************************************************************/

        Calendar TimeStamp = Calendar.getInstance();
        SimpleDateFormat TimeStampFormat = new SimpleDateFormat("yyyy MM dd::hh:mm:ss:SSS");

        int MeasurementLength = 8;        // This is the length of all measurements (including time) in bytes
        int IdLength = 4;                // This is the length of IDs in the byte stream

        byte databyte = 0;                // This is the data byte read from the stream
        int bytesread = 0;                // This is the number of bytes read from the stream

        long measurement;                // This is the word used to store all measurements - conversions are illustrated.
        int id;                          // This is the measurement id
        int i;                          // This is a loop counter

        double altitude = Double.NaN;
        double celsius = Double.NaN;
        boolean timeSet = false;

        List<ResultData> resultDataList = new ArrayList<>();

        /*************************************************************
         *	First we announce to the world that we are alive...
         **************************************************************/

        System.out.println("\n" + this.getName() + "::Sink Reading ");

        while (true) {
            try {
                /***************************************************************************
                 // We know that the first data coming to this filter is going to be an ID and
                 // that it is IdLength long. So we first decommutate the ID bytes.
                 ****************************************************************************/

                id = 0;

                for (i = 0; i < IdLength; i++) {
                    databyte = ReadFilterInputPort();    // This is where we read the byte from the stream...

                    id = id | (databyte & 0xFF);        // We append the byte on to ID...

                    if (i != IdLength - 1)                // If this is not the last byte, then slide the
                    {                                    // previously appended byte to the left by one byte
                        id = id << 8;                    // to make room for the next byte we append to the ID

                    }

                    bytesread++;                        // Increment the byte count

                } // for

                measurement = 0;

                for (i = 0; i < MeasurementLength; i++) {
                    databyte = ReadFilterInputPort();
                    measurement = measurement | (databyte & 0xFF);    // We append the byte on to measurement...

                    if (i != MeasurementLength - 1)                    // If this is not the last byte, then slide the
                    {                                                // previously appended byte to the left by one byte
                        measurement = measurement << 8;              // to make room for the next byte we append to the
                    }

                    bytesread++;                                    // Increment the byte count
                }

                if (id == 0) {
                    TimeStamp.setTimeInMillis(measurement);
                    timeSet = true;
                } else if (id == 2) {
                    altitude = Double.longBitsToDouble(measurement);
                } else if (id == 4) {
                    celsius = (Double.longBitsToDouble(measurement));
                }

                // Write to file once all values are available
                if (timeSet && !Double.isNaN(celsius) && !Double.isNaN(altitude)) {

                    ResultData resultData = new ResultData(TimeStamp.getTime(), celsius, altitude);
                    resultDataList.add(resultData);

                    // Reset so that next full set must be received
                    timeSet = false;
                    celsius = Double.NaN;
                    altitude = Double.NaN;
                }
            } catch (EndOfStreamException e) {
                writeOutputToFile(resultDataList);
                ClosePorts();
                System.out.println("\n" + this.getName() + "::Sink Exiting; bytes read: " + bytesread);
                break;
            }
        } // while
    } // run

    private void writeOutputToFile(List<ResultData> resultDataList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/OutputA.dat"))) {
            writer.write(String.format("%-20s %-18s %-15s%n", "Time:", "Temperature (C):", "Altitude (m):"));
            writer.write("-------------------------------------------------------------\n");

            for (ResultData data : resultDataList) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy:DD:HH:mm:ss");
                String formattedTime = formatter.format(data.getTime());

                String formattedTemp = String.format("%15.5f", data.getTemperature());
                String formattedAlt = String.format("%13.5f", data.getAltitude());

                writer.write(String.format("%-20s %s %s%n", formattedTime, formattedTemp, formattedAlt));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} // SingFilter