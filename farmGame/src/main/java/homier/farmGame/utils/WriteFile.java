package homier.farmGame.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class WriteFile{
	private String path;
	private boolean appendToFile = false;
	
	public WriteFile(String path){
		this.path = path;
	}
	
	public WriteFile(String path, boolean appendToFile){
		this.path = path;
		this.appendToFile = appendToFile;
	}
	
	public void writeToFile(String textLine) throws IOException {
		PrintWriter printLine = new PrintWriter(new FileWriter(path, appendToFile));
		
		printLine.printf("%s" + "%n", textLine);
		
		printLine.close();
	}
}