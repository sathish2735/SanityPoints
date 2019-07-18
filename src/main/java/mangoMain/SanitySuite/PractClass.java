package mangoMain.SanitySuite;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilities.GetProperty;
import utilities.ReusableAssets;
import utilities.SendEmail;

public class PractClass {

	static GetProperty prop = new GetProperty();
	public static String dir = System.getProperty("user.dir");
	public static WebDriver driver;
	public static WebDriverWait wait;
	public static String domainURL;
	public static void main(String[] args) throws IOException, MessagingException {

		String to[]= {prop.getProp("teamEmailAddress")};
		ReusableAssets.sendFinaReportEmail("mangorobot12@gmail.com", to, 2, 5, 1);
		System.out.println("Message is present");
		
	}
}
