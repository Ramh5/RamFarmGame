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
	
	/**
	 * 
	 * @param path : loads the file at the specified path
	 * @return the string in the file
	 */
	public static String getString(String path) {
		byte[] encoded = new byte[0];
		try {
			encoded = Files.readAllBytes(new File(path).toPath());
		} catch (IOException e) {
			System.out.println("error reading  file at " + path);
			e.printStackTrace();
		}
		return new String(encoded);
	}
}