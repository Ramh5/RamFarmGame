package homier.farmGame.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;

import homier.farmGame.controller.App;



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
	public static String getString(String resource) {
		System.out.println("read file: " + resource);
		byte[] encoded = new byte[0];
		try {
			InputStream in = App.class.getResourceAsStream(resource);
			encoded = IOUtils.toByteArray(in);
		} catch (IOException e) {
			System.out.println("error reading  file at " + resource);
			e.printStackTrace();
		}
		return new String(encoded);
	}
}