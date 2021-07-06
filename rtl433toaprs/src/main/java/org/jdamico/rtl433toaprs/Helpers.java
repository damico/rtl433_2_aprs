package org.jdamico.rtl433toaprs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Helpers {
	
	private static Helpers INSTANCE = null;
	private Helpers(){}
	public static Helpers getInstance(){
		if(null == INSTANCE) INSTANCE = new Helpers();
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

}
