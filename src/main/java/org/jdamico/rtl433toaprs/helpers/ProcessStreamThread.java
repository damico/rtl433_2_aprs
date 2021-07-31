package org.jdamico.rtl433toaprs.helpers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ProcessStreamThread extends Thread {
	InputStream in = null;
	ProcessBuilderHelper processBuilderHelper;
	public ProcessStreamThread(InputStream in) {
		this.in = in;
	}

	public void run() {

		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println("Return from RTL_433: "+line);
				//processBuilderHelper.jsonParser(latitude, longitude, tz, line);
			}
		} catch (Exception e) {
			System.err.println("Error calling : "+this.getClass().getName());
			System.err.println("Exception at "+this.getClass().getName()+" class: "+e.getMessage());
			System.exit(1);

		}finally {
			if(reader!=null) try{ reader.close(); }catch (Exception e) {e.printStackTrace();}
			if(in!=null) try{ in.close(); }catch (Exception e) {e.printStackTrace();}
		}
	}
}


