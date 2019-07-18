package mangoMain.SanitySuite;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.Status;

import utilities.ExtentTestManager;
import utilities.GetProperty;
import utilities.ReusableAssets;
import utilities.SendEmail;

public class PracticeClass {
	static GetProperty prop = new GetProperty();
	public static String dir = System.getProperty("user.dir");
	public static WebDriver driver;
	public static WebDriverWait wait;
	public static String domainURL;
	public static SendEmail myEmail = new SendEmail();
	public String dateName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());

	public static void main(String[] args) throws IOException, InterruptedException, AWTException, MessagingException {
		// TODO Auto-generated method stub
		System.setProperty("webdriver.chrome.driver", dir + prop.getProp("chromeDriver"));
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 15);
		driver.manage().window().maximize();
		driver.get("https://tech5group.engageexpress.com");
		PracticeClass myClass = new PracticeClass();
		domainURL = prop.getProp("newDomainURL");
		myClass.loginCheck();
		System.out.println("logged in");
		myClass.departmentCreationCheck();

	}

	public void departmentCreationCheck() {
		try {
			getURL("departmentTabURL");
			findElement(driver, "class", "departmentTools").click();
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("create-department")));
			findElement(driver, "class", "createDepartmentOption").click();
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("teamName")));
			String expectedDeptName = prop.getProp("departmentName") + dateName;
			findElement(driver, "id", "projectName").sendKeys(expectedDeptName);
			findElement(driver, "id", "shortDescription").sendKeys("This is to test department creation flow");
			findElement(driver, "linkText", "saveNContinue").click();
			explicitWait("linkText", "SaveNExit");
			findElement(driver, "linkText", "SaveNExit").click();
			Thread.sleep(1000);
			WebElement actualDeptName = driver
					.findElement(By.xpath("//span[contains(text(), '" + expectedDeptName + "')]"));
			if (actualDeptName.getText().equalsIgnoreCase(expectedDeptName)) {
				ExtentTestManager.getTest().log(Status.PASS, expectedDeptName + " Department is created Successfully");
				ExtentTestManager.getTest().log(Status.PASS, "Passed sampleTest Successfully");
				System.out.println("Department creation is successful");
			}
		} catch (Exception e) {
			System.out.println("Error in try block " + e.toString());
		}
	}

	public static String dateProvider() {
		Date date = new Date(); // your date
		// Choose time zone in which you want to interpret your Date
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, +1); // this makes calendar to point to tomorrow's date
		// int year = cal.get(Calendar.YEAR);
		// System.out.println(year);
		// int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.LONG);
		return Integer.toString(day);
	}

	public static void dragdrop(WebElement source, WebElement destination) {
		String xto = Integer.toString(destination.getLocation().x);
		String yto = Integer.toString(destination.getLocation().y);
		((JavascriptExecutor) driver).executeScript(
				"function simulate(f,c,d,e){var b,a=null;for(b in eventMatchers)if(eventMatchers[b].test(c)){a=b;break}if(!a)return!1;document.createEvent?(b=document.createEvent(a),a==\"HTMLEvents\"?b.initEvent(c,!0,!0):b.initMouseEvent(c,!0,!0,document.defaultView,0,d,e,d,e,!1,!1,!1,!1,0,null),f.dispatchEvent(b)):(a=document.createEventObject(),a.detail=0,a.screenX=d,a.screenY=e,a.clientX=d,a.clientY=e,a.ctrlKey=!1,a.altKey=!1,a.shiftKey=!1,a.metaKey=!1,a.button=1,f.fireEvent(\"on\"+c,a));return!0} var eventMatchers={HTMLEvents:/^(?:load|unload|abort|error|select|change|submit|reset|focus|blur|resize|scroll)$/,MouseEvents:/^(?:click|dblclick|mouse(?:down|up|over|move|out))$/}; "
						+ "simulate(arguments[0],\"mousedown\",0,0); simulate(arguments[0],\"mousemove\",arguments[1],arguments[2]); simulate(arguments[0],\"mouseup\",arguments[1],arguments[2]); ",
				source, xto, yto);
		System.out.println("Performed the action");
	}

	public void enableUserModule(WebDriver driver, String moduleName) throws IOException {
		getURL("moduleSetupURL");
		for (int moduleNo = 1; moduleNo < 33; moduleNo++) {
			String currentModuleName = driver
					.findElement(By.xpath("//*[@id=\"all_users\"]/div/ul/li[" + moduleNo + "]/div[2]/div/div"))
					.getText();
			System.out.println(currentModuleName);
			if (currentModuleName.equalsIgnoreCase(moduleName)) {
				String enableStatus = driver
						.findElement(By.xpath("//*[@id=\"all_users\"]/div/ul/li[" + moduleNo + "]/div[3]/div/input"))
						.getAttribute("data-enable");
				System.out.println(enableStatus);
				if (enableStatus.equalsIgnoreCase("true") || enableStatus.equalsIgnoreCase("Y")) {
					WebElement enableCheck = driver
							.findElement(By.xpath("//*[@id=\"all_users\"]/div/ul/li[" + (moduleNo - 4) + "]/div[3]"));
					scrollToView(driver, enableCheck);
					driver.findElement(By.xpath("//*[@id=\"all_users\"]/div/ul/li[" + moduleNo + "]/div[3]")).click();
				} else {
					System.out.println("Its already enabled");
				}
				break;
			}
		}
	}

	public void userOptions(WebDriver driver, String desiredDropdownAction) {
		// Need to use only text link to find element here
		try {
			explicitWait("cssSelector", "userSettings");
			findElement(driver, "cssSelector", "userSettings").click();
			mouseHover(driver, findElement(driver, "linkText", desiredDropdownAction));
			findElement(driver, "linkText", desiredDropdownAction).click();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static int randomNumber() {
		List<Integer> numbers = new ArrayList<Integer>();
		Random random = new Random();
		System.out.println(random.nextInt(100));
		int next = random.nextInt(100);
		int value = 0;
		if (!numbers.contains(next)) {
			numbers.add(next);
			value = next;
		} else {
			randomNumber();
		}
		return value;
	}

	public static WebElement findElement(WebDriver driver, String locatorType, String locatorName) throws IOException {
		WebElement element = null;
		if (locatorType == "xpath") {
			element = driver.findElement(By.xpath(prop.getElement(locatorName)));
		} else if (locatorType == "name") {
			element = driver.findElement(By.name(prop.getElement(locatorName)));
		} else if (locatorType == "id") {
			element = driver.findElement(By.id(prop.getElement(locatorName)));
		} else if (locatorType == "class") {
			element = driver.findElement(By.className(prop.getElement(locatorName)));
		} else if (locatorType == "cssSelector") {
			element = driver.findElement(By.cssSelector(prop.getElement(locatorName)));
		} else if (locatorType == "linkText") {
			element = driver.findElement(By.linkText(prop.getElement(locatorName)));
		} else if (locatorType == "partialLinkText") {
			element = driver.findElement(By.partialLinkText(prop.getElement(locatorName)));
		} else if (locatorType == "tagName") {
			element = driver.findElement(By.tagName(prop.getElement(locatorName)));
		} else {
			System.out.println("Please provide valid locator Type");
		}
		return element;
	}

	public static void mouseHover(WebDriver driver, WebElement element) {
		String strJavaScript = "var element = arguments[0]; var mouseEventObj = document.createEvent('MouseEvents'); mouseEventObj.initEvent( 'mouseover', true, true ); element.dispatchEvent(mouseEventObj);";
		((JavascriptExecutor) driver).executeScript(strJavaScript, element);
	}

	public void loginCheck() {
		try {
			findElement(driver, "id", "userID").sendKeys(prop.getProp("emailForSignUP"));
			findElement(driver, "id", "password").sendKeys(prop.getProp("GodPassword"));
			findElement(driver, "id", "loginButton").click();
			explicitWait("id", "globalSearchBox");
			System.out.println("logged in");
			ExtentTestManager.getTest().log(Status.PASS, "Logged In Successfully");
		} catch (Exception e) {
			System.out.println("Error in try block " + e.toString());
		}
	}

	public static void explicitWait(String locatorType, String locatorName) throws IOException {
		// always pass xpath of element to be located to this method
		wait.until(ExpectedConditions.elementToBeClickable(findElement(driver, locatorType, locatorName)));
		System.out.println("waited for element");
	}

	public void composeClick(String ActionName) throws InterruptedException, IOException {
		// PLEASE PROVIDE THE KEYWORD/ACTION NAME IN CAPITAL LETTERS
		final String opt1 = "UPDATE";
		final String opt2 = "QUESTION";
		final String opt3 = "POST";
		final String opt4 = "MESSAGES";
		final String opt5 = "TASK";
		final String opt6 = "EVENT";
		final String opt7 = "POLL";
		final String opt8 = "SURVEY";
		final String opt9 = "QUIZ";
		final String opt10 = "IDEA";
		WebElement compose = driver.findElement(By.xpath("//*[@id=\"ms-top-def-nav\"]/li/div/a"));
		WebElement update = driver.findElement(By.xpath("//*[@id=\"ms-top-def-nav\"]/li/ul/li[1]/a/i"));
		WebElement question = driver.findElement(By.xpath("//*[@id=\"ms-top-def-nav\"]/li/ul/li[2]/a/i"));
		System.out.println("Entered into compose box");
		compose.click();
		wait.until(ExpectedConditions.visibilityOf(question));
		WebElement notify = driver.findElement(By.xpath("//*[@id=\"notice\"]/a"));
		try {
			notify.click();
			System.out.println("closed the notification");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		switch (ActionName) {
		case opt1:
			mouseHover(driver, update);
			update.click();
			Thread.sleep(1000);
			// WebElement updateTo = driver.findElement(By.id("token-input-myTeams"));
			// keyDown(driver, updateTo, "TestAuto");
			driver.findElement(By.id("project_status_update_team")).sendKeys("This is a test update");
			driver.findElement(By.xpath("//*[@id=\"ms-feed-btn\"]/span/span")).click();
			break;
		case opt2:
			mouseHover(driver, question);
			question.click();
			wait.until(ExpectedConditions.visibilityOf(findElement(driver, "id", "yourQuestion")));
			findElement(driver, "xpath", "toAllFollowers").click();
			// WebElement QuestionTo =
			// driver.findElement(By.xpath("token-input-myquestionTeams"));
			// keyDown(driver, QuestionTo, "TestAuto");
			findElement(driver, "id", "yourQuestion").sendKeys("Are you capable of Automating all modules??");
			findElement(driver, "xpath", "ask").click();
			break;
		case opt4:
			driver.findElement(By.xpath("//*[@id=\"file_dimension_default\"]/li[1]/p[2]/textarea"))
					.sendKeys("uploading image is for test");
			driver.findElement(By.id("tag-it_input_c")).sendKeys("awesome");
			Thread.sleep(1000);
			driver.findElement(By.id("tag-it_input_c")).sendKeys(Keys.ENTER);
			Select sel = new Select(driver.findElement(By.id("attachments_attachment_id_658_")));
			sel.selectByValue("14238");
			WebElement sendButton = driver.findElement(By.xpath("//*[@id=\"ms-feed-btn\"]/span/span"));
			sendButton.click();
			break;
		default:
			System.out.println("Please provide valid element");
			break;
		}

	}

	public void askQuestionCheck() throws InterruptedException, IOException {
		composeClick("QUESTION");
		System.out.println("posted question");

	}

	public static void getURL(String locatorName) throws IOException {
		driver.get(domainURL + prop.getProp(locatorName));
	}

	public static void scrollToView(WebDriver driver, WebElement element) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
	}

	public void defaultGroupsCheck() {
		try {
			getURL("groupsTabURL");
			Thread.sleep(2000);
			List<String> defaultGroups = Arrays.asList("Intranet Admins", "Everyone", "Domain Admins",
					"Idea Management Team");
			for (int i = 1; i < 5; i++) {
				String locatorName = "//*[@id='project_description']/div[" + i + "]/a/div/div[2]/p[1]/span/span";
				String groupName = driver.findElement(By.xpath(locatorName)).getText();
				if (defaultGroups.contains(groupName)) {
					System.out.println("all groups are present");
				}
			}
		} catch (Exception e) {
			System.out.println("Error in try block " + e.toString());
		}
	}
}
