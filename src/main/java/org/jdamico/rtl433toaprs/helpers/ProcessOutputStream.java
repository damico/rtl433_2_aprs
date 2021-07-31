package org.jdamico.rtl433toaprs.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

public class ProcessOutputStream extends OutputStream {
	private Process process;
	private Thread outWriterThread;
	private Thread errWriterThread;

	public ProcessOutputStream(String[] cmd) throws IOException {
		this(new ProcessBuilder(cmd), new PrintWriter(System.out), new PrintWriter(System.err));
	}

	public ProcessOutputStream(String[] cmd, Writer writer) throws IOException {
		this(new ProcessBuilder(cmd), writer, writer);
	}

	public ProcessOutputStream(String[] cmd, Writer output, Writer error) throws IOException {
		this(new ProcessBuilder(cmd), output, error);
	}

	public ProcessOutputStream(ProcessBuilder builder, Writer output, Writer error) throws IOException {
		this.process = builder.start();

		
		
		//errWriterThread = new StreamGobbler(process.getErrorStream(), error);
		
		
		
		
		outWriterThread = new ProcessStreamThread(process.getInputStream());
		//errWriterThread.start();
		outWriterThread.start();
	}

	@Override
	public void flush() throws IOException {
		process.getOutputStream().flush();
	}

	@Override
	public void write(int b) throws IOException {
		process.getOutputStream().write(b);
	}

	@Override
	public void close() throws IOException {
		process.getOutputStream().close();
		try {
			errWriterThread.join();
			outWriterThread.join();
			process.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

} // end static class StaticOutputStream

