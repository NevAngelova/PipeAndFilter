import java.io.*;

public class FilterFramework extends Thread
{
	// Define filter input and output ports

	private PipedInputStream InputReadPort = new PipedInputStream();
	private PipedOutputStream OutputWritePort = new PipedOutputStream();

	// The following reference to a filter is used because java pipes are able to reliably
	// detect broken pipes on the input port of the filter. This variable will point to
	// the previous filter in the network and when it dies, we know that it has closed its
	// output pipe and will send no more data.

	private FilterFramework InputFilter;

	/***************************************************************************
	* InnerClass:: EndOfStreamExeception
	* Purpose: This
	*
	*
	*
	* Arguments: none
	*
	* Returns: none
	*
	* Exceptions: none
	*
	****************************************************************************/

	class EndOfStreamException extends Exception {

		EndOfStreamException () { super(); }

		EndOfStreamException(String s) { super(s); }

	} // class


	/***************************************************************************
	* CONCRETE METHOD:: Connect
	* Purpose: This method connects filters to each other. All connections are
	* through the inputport of each filter. That is each filter's inputport is
	* connected to another filter's output port through this method.
	*
	* Arguments:
	* 	FilterFramework - this is the filter that this filter will connect to.
	*
	* Returns: void
	*
	* Exceptions: IOException
	*
	****************************************************************************/

	void Connect( FilterFramework Filter )
	{
		try
		{
			// Connect this filter's input to the upstream pipe's output stream

			InputReadPort.connect( Filter.OutputWritePort );
			InputFilter = Filter;

		} // try

		catch( Exception Error )
		{
			System.out.println( "\n" + this.getName() + " FilterFramework error connecting::"+ Error );

		} // catch

	} // Connect

	/***************************************************************************
	* CONCRETE METHOD:: ReadFilterInputPort
	* Purpose: This method reads data from the input port one byte at a time.
	*
	* Arguments: void
	*
	* Returns: byte of data read from the input port of the filter.
	*
	* Exceptions: IOExecption, EndOfStreamException (rethrown)
	*
	****************************************************************************/

	byte ReadFilterInputPort() throws EndOfStreamException
	{
		byte datum = 0;

		/***********************************************************************
		* Since delays are possible on upstream filters, we first wait until
		* there is data available on the input port. We check,... if no data is
		* available on the input port we wait for a quarter of a second and check
		* again. Note there is no timeout enforced here at all and if upstream
		* filters are deadlocked, then this can result in infinite waits in this
		* loop. It is necessary to check to see if we are at the end of stream
		* in the wait loop because it is possible that the upstream filter completes
		* while we are waiting. If this happens and we do not check for the end of
		* stream, then we could wait forever on an upstream pipe that is long gone.
		* Unfortunately Java pipes do not throw exceptions when the input pipe is
		* broken.
		***********************************************************************/

		try
		{
			while (InputReadPort.available()==0 )
			{
				if (EndOfInputStream())
				{
					throw new EndOfStreamException("End of input stream reached");

				} //if

				sleep(250);

			} // while

		} // try

		catch( EndOfStreamException Error )
		{
			throw Error;

		} // catch

		catch( Exception Error )
		{
			System.out.println( "\n" + this.getName() + " Error in read port wait loop::" + Error );

		} // catch

		/***********************************************************************
		* If at least one byte of data is available on the input
		* pipe we can read it. We read and write one byte to and from ports.
		***********************************************************************/

		try
		{
			datum = (byte)InputReadPort.read();
			return datum;

		} // try

		catch( Exception Error )
		{
			System.out.println( "\n" + this.getName() + " Pipe read error::" + Error );
			return datum;

		} // catch

	} // ReadFilterPort

	/***************************************************************************
	* CONCRETE METHOD:: WriteFilterOutputPort
	* Purpose: This method writes data to the output port one byte at a time.
	*
	* Arguments:
	* 	byte datum - This is the byte that will be written on the output port.of
	*	the filter.
	*
	* Returns: void
	*
	* Exceptions: IOException
	*
	****************************************************************************/

	void WriteFilterOutputPort(byte datum)
	{
		try
		{
            OutputWritePort.write((int) datum );
		   	OutputWritePort.flush();

		} // try

		catch( Exception Error )
		{
			System.out.println("\n" + this.getName() + " Pipe write error::" + Error );

		} // catch

		return;

	} // WriteFilterPort

	/***************************************************************************
	* CONCRETE METHOD:: EndOfInputStream
	* Purpose: This method is used within this framework which is why it is private
	* It returns a true when there is no more data to read on the input port of
	* the instance filter. What it really does is to check if the upstream filter
	* is still alive. This is done because Java does not reliably handle broken
	* input pipes and will often continue to read (junk) from a broken input pipe.
	*
	* Arguments: void
	*
	* Returns: A value of true if the previous filter has stopped sending data,
	*		   false if it is still alive and sending data.
	*
	* Exceptions: none
	*
	****************************************************************************/

	private boolean EndOfInputStream()
	{
		if (InputFilter.isAlive())
		{
			return false;

		} else {

			return true;

		} // if

	} // EndOfInputStream

	/***************************************************************************
	* CONCRETE METHOD:: ClosePorts
	* Purpose: This method is used to close the input and output ports of the
	* filter. It is important that filters close their ports before the filter
	* thread exits.
	*
	* Arguments: void
	*
	* Returns: void
	*
	* Exceptions: IOExecption
	*
	****************************************************************************/

	void ClosePorts()
	{
		try
		{
			InputReadPort.close();
			OutputWritePort.close();

		}
		catch( Exception Error )
		{
			System.out.println( "\n" + this.getName() + " ClosePorts error::" + Error );

		} // catch

	} // ClosePorts

	/***************************************************************************
	* CONCRETE METHOD:: run
	* Purpose: This is actually an abstract method defined by Thread. It is called
	* when the thread is started by calling the Thread.start() method. In this
	* case, the run() method should be overridden by the filter programmer using
	* this framework superclass
	*
	* Arguments: void
	*
	* Returns: void
	*
	* Exceptions: IOExecption
	*
	****************************************************************************/

	public void run()
    {
		// The run method should be overridden by the subordinate class. Please
		// see the example applications provided for more details.

	} // run

} // FilterFramework class