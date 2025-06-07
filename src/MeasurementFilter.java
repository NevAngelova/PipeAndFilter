public abstract class MeasurementFilter extends FilterFramework {

    protected abstract int targetId();

    protected abstract double transform(double input);

    protected abstract String filterName();

    public void run() {

        int bytesread = 0;                    // Number of bytes read from the input file.
        int byteswritten = 0;                // Number of bytes written to the stream.
        byte databyte = 0;                    // The byte of data read from the file

        int MeasurementLength = 8;        // This is the length of all measurements (including time) in bytes
        int IdLength = 4;                // This is the length of IDs in the byte stream

        long measurement;                // This is the word used to store all measurements - conversions are illustrated.
        int id;                            // This is the measurement id
        int i;                            // This is a loop counter

        // Next we write a message to the terminal to let the world know we are alive...

        System.out.println("\n" + this.getName() + "::" + this.filterName() + " Reading");

        while (true) {
            try {

                id = 0;

                for (i = 0; i < IdLength; i++) {
                    databyte = ReadFilterInputPort();    // This is where we read the byte from the stream...

                    id = id | (databyte & 0xFF);        // We append the byte on to ID...

                    if (i != IdLength - 1)                // If this is not the last byte, then slide the
                    {                                    // previously appended byte to the left by one byte
                        id = id << 8;                    // to make room for the next byte we append to the ID
                    }

                    bytesread++;

                    WriteFilterOutputPort(databyte);
                    byteswritten++;
                }

                measurement = 0;
                byte[] measurementBytes = new byte[MeasurementLength];

                for (i = 0; i < MeasurementLength; i++) {
                    databyte = ReadFilterInputPort();
                    measurementBytes[i] = databyte;

                    measurement = measurement | (databyte & 0xFF);    // We append the byte on to measurement...

                    if (i != MeasurementLength - 1)                    // If this is not the last byte, then slide the
                    {                                                // previously appended byte to the left by one byte
                        measurement = measurement << 8;              // to make room for the next byte we append to the
                    }

                    bytesread++;
                }

                if (id == targetId()) {

                    double value = Double.longBitsToDouble(measurement);
                    double transformed = transform(value);
                    measurement = Double.doubleToLongBits(transformed);

                    //  System.out.println( " ID = " + id + " " + Double.longBitsToDouble(measurement));

                    // Convert measurement back to bytes (big-endian)
                    for (i = MeasurementLength - 1; i >= 0; i--) {
                        measurementBytes[i] = (byte) (measurement & 0xFF);
                        measurement >>= 8;
                    }
                }

                // Write measurement (original or converted)
                for (i = 0; i < MeasurementLength; i++) {
                    WriteFilterOutputPort(measurementBytes[i]);
                    byteswritten++;
                }
            } // try

            catch (EndOfStreamException e) {
                ClosePorts();

                System.out.println("\n" + this.getName() + "::" + this.filterName() + " Exiting; bytes read: " + bytesread + " bytes written: " + byteswritten);
                break;

            } // catch

        } // while

    } // run
}