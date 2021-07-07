package org.jdamico.rtl433toaprs.helpers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class IOHelper {
	
	private static IOHelper INSTANCE = null;
	private IOHelper(){}
	public static IOHelper getInstance(){
		if(null == INSTANCE) INSTANCE = new IOHelper();
		return INSTANCE;
	}
	
	public String readTextFileToString(File source) throws Exception {
		StringBuffer output = null;

		

		if(source != null && source.exists() && source.isFile()) {
			Path path = source.toPath();
			try {
				List<String> lst = Files.readAllLines(path);
				output = new StringBuffer();
				for (int i = 0; i < lst.size(); i++) {
					output.append(lst.get(i)+"\n");
				}
			} catch (IOException e) {
				throw new Exception("invalid file");
			}
		}else throw new Exception("invalid file");



		return output.toString();
	}

	public void writeStrToFile(String str, String fileName) throws Exception{

		FileWriter fw = null;
		BufferedWriter out = null;
		try {
			fw = new FileWriter(fileName);
			out = new BufferedWriter(fw);
			out.write(str);  
		}
		catch (IOException e)
		{
			throw new Exception(e);

		}
		finally
		{
			if(out != null)
				try {
					out.close();
				} catch (IOException e) {
					throw new Exception(e);
				}
			if(fw != null)
				try {
					fw.close();
				} catch (IOException e) {
					throw new Exception(e);
				}
		}	
	}
	
}
