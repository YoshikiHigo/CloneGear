package yoshikihigo.clonegear.gui;

import java.io.PrintStream;

public class MessagePrinter {

	static final public MessagePrinter OUT = new MessagePrinter(System.out);
	static final public MessagePrinter ERR = new MessagePrinter(System.err);
	static final public MessagePrinter LOG = new MessagePrinter(System.out);

	public void setStream(final PrintStream stream) {
		this.stream = stream;
	}

	public void println(final String str) {
		this.stream.println(str);
	}

	public void print(final String str) {
		this.stream.print(str);
	}

	private MessagePrinter(final PrintStream stream) {
		this.stream = stream;
	}

	private PrintStream stream;
}
