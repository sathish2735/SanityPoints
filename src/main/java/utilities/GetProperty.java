package utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class GetProperty {
	static FileInputStream fin;
	static String dir = System.getProperty("user.dir");
	

	public String getProp(String input) throws IOException {
		Properties prop = new Properties();
		String filePath = dir + "\\src/main/java\\utilities\\config.properties";
		fin = new FileInputStream(filePath);
		prop.load(fin);
		String myProp = prop.getProperty(input);
		fin.close();
		return myProp;
		}
	
	  public String getElement(String element) throws IOException {
		Properties prop1 = new Properties();
		String filePath = dir + "\\src/main/java\\utilities\\Elements.properties";
		fin = new FileInputStream(filePath);
		prop1.load(fin);
		String myElement = prop1.getProperty(element);
		fin.close();
		return myElement;
	  }
	 
}
