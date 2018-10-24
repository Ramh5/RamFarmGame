package homier.farmGame.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class ReadFile{
	
	private String path;
	
	public ReadFile(String filePath){
		path = filePath;
	}
	
	/**
	 * 
	 * @return the string reprensentation of the file
	 * @throws IOException
	 */
	public String getString() throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded);
	}
	
	static public String getString(String path) throws IOException {
		byte[] encoded = Files.readAllBytes(new File(path).toPath());
		return new String(encoded);
	}
}