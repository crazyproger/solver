package org.matheclipse.gwt.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public class WriterOutputStream extends OutputStream {
	protected Writer fWriter;

	protected String fEncoding;

	private byte fBuffer[];

	public WriterOutputStream(final Writer writer, final String encoding) {
		fBuffer = new byte[1];
		fWriter = writer;
		fEncoding = encoding;
	}

	public WriterOutputStream(final Writer writer) {
		fBuffer = new byte[1];
		fWriter = writer;
	}

	public void close() throws IOException {
		fWriter.close();
		fWriter = null;
		fEncoding = null;
	}

	public void flush() throws IOException {
		fWriter.flush();
	}

	public void write(final byte b[]) throws IOException {
		if (fEncoding == null) {
			fWriter.write(new String(b));
		} else {
			fWriter.write(new String(b, fEncoding));
		}
	}

	public void write(final byte b[], final int off, final int len)
			throws IOException {
		if (fEncoding == null) {
			fWriter.write(new String(b, off, len));
		} else {
			fWriter.write(new String(b, off, len, fEncoding));
		}
	}

	public synchronized void write(final int b) throws IOException {
		fBuffer[0] = (byte) b;
		write(fBuffer);
	}

}
