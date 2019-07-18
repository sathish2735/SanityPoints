package utilities;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.TimeZone;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.Status;

public class ReusableAssets {
	public static GetProperty prop = new GetProperty();
	public static String dir = System.getProperty("user.dir");
	public String expectedPostTitle="";
	public static int totalpass;
	public static int totalfail;
	public static int totalskip;
	public WebElement findElement(WebDriver driver, String locatorType, String locatorName) {
		WebElement element = null;
		try {
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
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return element;
	}

	public void mouseHover(WebDriver driver, WebElement element) {
		String strJavaScript = "var element = arguments[0]; var mouseEventObj = document.createEvent('MouseEvents'); mouseEventObj.initEvent( 'mouseover', true, true ); element.dispatchEvent(mouseEventObj);";
		((JavascriptExecutor) driver).executeScript(strJavaScript, element);
	}

	public void scrollToView(WebDriver driver, WebElement element) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
	}

	public void upload(WebDriver driver, String filePath) {
		try {
			// Store the location of the file in clipboard
			// Clipboard
			StringSelection strSel = new StringSelection(dir + prop.getProp(filePath));
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(strSel, null);

			// Create an object for robot class
			Robot robot = new Robot();
			// Control key in the keyboard
			// Ctrl+V
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			Thread.sleep(2000);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
			ExtentTestManager.getTest().log(Status.PASS,
					"Uploaded Given file located at path " + filePath + " Successfully");
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "Unable to complete the upload action " + e.getMessage());
		}
	}

	public void keyDown(WebDriver driver, WebElement element, String text) throws InterruptedException {
		String downEnter = Keys.chord(Keys.ARROW_DOWN, Keys.ENTER);
		element.sendKeys(text);
		Thread.sleep(2000);
		element.sendKeys(downEnter);
	}

	public String getMethodName() {
		String nameofCurrMethod = new Exception().getStackTrace()[0].getMethodName();
		return nameofCurrMethod;
	}

	public void feedSelector(WebDriver driver, String feedType) throws IOException, InterruptedException {
		findElement(driver, "id", "feedSelector").click();
		if (feedType.equalsIgnoreCase("Unread")) {
			WebElement unread = findElement(driver, "id", "unreadFeeds");
			Thread.sleep(1000);
			mouseHover(driver, unread);
			unread.click();
		} else if (feedType.equalsIgnoreCase("MyFeeds")) {
			WebElement myFeeds = findElement(driver, "id", "myFeeds");
			Thread.sleep(1000);
			mouseHover(driver, myFeeds);
			myFeeds.click();
		} else if (feedType.equalsIgnoreCase("AllFeeds")) {
			WebElement allFeeds = findElement(driver, "id", "allFeeds");
			Thread.sleep(1000);
			mouseHover(driver, allFeeds);
			allFeeds.click();
		}
	}

	public void userOptions(WebDriver driver, String desiredDropdownAction) {
		// Need to use only text link to find element here
		// desiredDropdownAction= goToUserPortal, gamificationSection, viewMyProfile,
		// manageDomain, signOut, signOutAllOther
		try {
			findElement(driver, "cssSelector", "userSettings").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on User Settings dropdown");
			Thread.sleep(1000);
			mouseHover(driver, findElement(driver, "linkText", desiredDropdownAction));
			ExtentTestManager.getTest().log(Status.INFO, "Moved the cursor on to " + desiredDropdownAction);
			findElement(driver, "linkText", desiredDropdownAction).click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on " + desiredDropdownAction);
		} catch (Exception e) {

			ExtentTestManager.getTest().log(Status.FAIL,
					"Unable to perform desired action from UserSettings drop down");
		}
	}

	public int randomNumber() {
		List<Integer> numbers = new ArrayList<Integer>();
		Random random = new Random();
		int next = random.nextInt(100000);
		int value = 0;
		if (!numbers.contains(next)) {
			numbers.add(next);
			value = next;
		} else {
			randomNumber();
		}
		return value;
	}

	public String dateProvider() {
		Date date = new Date(); // your date
		// Choose time zone in which you want to interpret your Date
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, +1); // this makes calendar to point to tomorrow's date
		// int year = cal.get(Calendar.YEAR);
		// System.out.println(year);
		// int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		return Integer.toString(day);
	}

	public void explicitWait(WebDriver driver, String locatorType, String locatorName) throws IOException {
		WebDriverWait wait = new WebDriverWait(driver, 15);
		wait.until(ExpectedConditions.elementToBeClickable(findElement(driver, locatorType, locatorName)));
		ExtentTestManager.getTest().log(Status.INFO, "wait was activated until desired element is visible on the page");
	}

	public void composeClick(WebDriver driver, WebDriverWait wait, String ActionName)
			throws InterruptedException, IOException {
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
		final String opt11 = "CAMPAIGN";
		final String opt12 = "RECOGNIZE";
		final String opt13 = "OPPORTUNITY";
		final String opt14 = "WIKIS";
		final String opt15 = "FORM";
		final String opt16 = "REMINDER";
		final String opt17 = "MEDIA";
		final String opt18 = "NOTE";
		final String opt19 = "GREETING";
		final String opt20 = "SCREENSHARE";
		final String opt21 = "BALLOT";
		Thread.sleep(1000);
		WebElement notify = driver.findElement(By.xpath("//*[@id=\"notice\"]/a"));
		try {
			notify.click();
			ExtentTestManager.getTest().log(Status.INFO, "Closed the Notification bar");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		explicitWait(driver, "linkText", "composeBoxButton");
		findElement(driver, "linkText", "composeBoxButton").click();
		WebElement update = driver.findElement(By.xpath("//*[@class=\"dropdown-menu composebox_menu\"]/li[1]/a"));
		WebElement question = driver.findElement(By.xpath("//*[@class=\"dropdown-menu composebox_menu\"]/li[2]/a"));
		ExtentTestManager.getTest().log(Status.INFO, "Clicked on Compose Box");
		wait.until(ExpectedConditions.visibilityOf(question));
		ExtentTestManager.getTest().log(Status.INFO, "Compose Box is open now");
		WebElement post = driver.findElement(By.xpath("//*[@class=\"dropdown-menu composebox_menu\"]/li[3]/a"));
		WebElement message = driver.findElement(By.xpath("//*[@class=\"dropdown-menu composebox_menu\"]/li[4]/a"));
		WebElement task = driver.findElement(By.xpath("//*[@class=\"dropdown-menu composebox_menu\"]/li[5]/a"));
		WebElement event = driver.findElement(By.xpath("//*[@class=\"dropdown-menu composebox_menu\"]/li[6]/a"));
		WebElement poll = driver.findElement(By.xpath("//*[@class=\"dropdown-menu composebox_menu\"]/li[7]/a"));
		WebElement survey = driver.findElement(By.xpath("//*[@class=\"dropdown-menu composebox_menu\"]/li[8]/a"));
		WebElement quiz = driver.findElement(By.xpath("//*[@class=\"dropdown-menu composebox_menu\"]/li[9]/a"));
		WebElement idea = driver.findElement(By.xpath("//*[@class=\"dropdown-menu composebox_menu\"]/li[10]/a"));
		WebElement campaign = driver.findElement(By.xpath("//*[@class=\"dropdown-menu composebox_menu\"]/li[11]/a"));
		WebElement recognize = driver.findElement(By.xpath("//*[@class=\"dropdown-menu composebox_menu\"]/li[12]/a"));
		WebElement opportunity = driver.findElement(By.xpath("//*[@class=\"dropdown-menu composebox_menu\"]/li[13]/a"));
		WebElement wikis = driver.findElement(By.xpath("//*[@class=\"dropdown-menu composebox_menu\"]/li[14]/a"));
		WebElement form = driver.findElement(By.xpath("//*[@class=\"dropdown-menu composebox_menu\"]/li[15]/a"));
		WebElement reminder = driver.findElement(By.xpath("//*[@class=\"dropdown-menu composebox_menu\"]/li[16]/a"));
		WebElement media = driver.findElement(By.xpath("//*[@class=\"dropdown-menu composebox_menu\"]/li[17]/a"));
		WebElement note = driver.findElement(By.xpath("//*[@class=\"dropdown-menu composebox_menu\"]/li[18]/a"));
		WebElement greeting = driver.findElement(By.xpath("//*[@class=\"dropdown-menu composebox_menu\"]/li[19]/a"));
		WebElement screenShare = driver.findElement(By.xpath("//*[@class=\"dropdown-menu composebox_menu\"]/li[20]/a"));
		WebElement ballot = driver.findElement(By.xpath("//*[@class=\"dropdown-menu composebox_menu\"]/li[21]/a"));

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
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Question Successfully");
			explicitWait(driver, "id", "yourQuestion");
			findElement(driver, "xpath", "toAllFollowers").click();
			ExtentTestManager.getTest().log(Status.INFO, "Clicked on To all my followers");
			findElement(driver, "id", "yourQuestion").sendKeys(prop.getProp("questionDescription"));
			ExtentTestManager.getTest().log(Status.PASS, "Entered the description of the question");
			Thread.sleep(2000);
			findElement(driver, "xpath", "askButton").click();
			Thread.sleep(1000);
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Ask Button Successfully");
			break;
		case opt3:
			mouseHover(driver, post);
			post.click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Post Successfully");
			Thread.sleep(1000);
			findElement(driver, "class", "shareWithEveryone").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Share with Everyone Successfully");
			findElement(driver, "xpath", "postTemplateSelect").click();
			findElement(driver, "id", "postContinueButton").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Continue Button");
			explicitWait(driver, "id", "postTitle");
			WebElement postTitle = findElement(driver, "id", "postTitle");
			postTitle.clear();
			expectedPostTitle = prop.getProp("postTitle") + System.currentTimeMillis();
			postTitle.sendKeys(expectedPostTitle);
			ExtentTestManager.getTest().log(Status.PASS, "Entered the Post Title As: " + expectedPostTitle);
			Thread.sleep(2000);
			findElement(driver, "xpath", "publishPostButton").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on publish Post Button");
			break;
		case opt4:
			mouseHover(driver, message);
			message.click();
			Thread.sleep(1000);
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Message Successfully");
			wait.until(ExpectedConditions.visibilityOf(findElement(driver, "id", "")));
			WebElement mailTo = driver.findElement(By.id("token-input-direct_user_field"));
			keyDown(driver, mailTo, "Satheesh");
			driver.findElement(By.id("direct_subject_field")).sendKeys("This is a test mail");
			driver.findElement(By.id("project_status_update")).sendKeys("This mail is just to check the functionality");

			driver.findElement(By.xpath("//*[@id=\"project_status_update\"]/following-sibling::div[3]")).click();
			Thread.sleep(2000);
			WebElement gifimage = driver
					.findElement(By.xpath("//*[@id=\"mango-gif-picker--menu-items\"]/div/div/div[1]/a[1]"));
			wait.until(ExpectedConditions.visibilityOf(gifimage));
			try {
				gifimage.click();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

			driver.findElement(By.xpath("//*[@id=\"frm-dm\"]/div[4]/div/div[2]/div[1]/a")).click();
			Thread.sleep(1000);
			WebElement Done = driver.findElement(By.id("btn_file_upload"));
			scrollToView(driver, Done);
			Thread.sleep(500);
			driver.findElement(By.id("fileupload")).click();
			Thread.sleep(2000);
			upload(driver, prop.getProp("filePath1"));
			Thread.sleep(2000);

			driver.findElement(By.xpath("//*[@id=\"file_dimension_default\"]/li[1]/p[2]/textarea"))
					.sendKeys("uploading image is for test");
			driver.findElement(By.id("tag-it_input_c")).sendKeys("awesome");
			Thread.sleep(1000);
			driver.findElement(By.id("tag-it_input_c")).sendKeys(Keys.ENTER);
			Select sel = new Select(driver.findElement(By.id("attachments_attachment_id_658_")));
			sel.selectByValue("14238");
			Done.click();
			WebElement sendButton = driver.findElement(By.xpath("//*[@id=\"ms-feed-btn\"]/span/span"));
			sendButton.click();
			break;
		case opt5:
			mouseHover(driver, task);
			task.click();
			Thread.sleep(1000);
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Post Successfully");
			wait.until(ExpectedConditions.visibilityOf(findElement(driver, "id", "")));
			break;
		case opt6:
			mouseHover(driver, event);
			event.click();
			Thread.sleep(1000);
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Post Successfully");
			wait.until(ExpectedConditions.visibilityOf(findElement(driver, "id", "")));
			break;
		case opt7:
			mouseHover(driver, poll);
			poll.click();
			Thread.sleep(1000);
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Post Successfully");
			wait.until(ExpectedConditions.visibilityOf(findElement(driver, "id", "")));
			break;
		case opt8:
			mouseHover(driver, survey);
			survey.click();
			Thread.sleep(1000);
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Post Successfully");
			wait.until(ExpectedConditions.visibilityOf(findElement(driver, "id", "")));
			break;
		case opt9:
			mouseHover(driver, quiz);
			quiz.click();
			Thread.sleep(1000);
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Post Successfully");
			wait.until(ExpectedConditions.visibilityOf(findElement(driver, "id", "")));
			break;
		case opt10:
			mouseHover(driver, idea);
			idea.click();
			Thread.sleep(1000);
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Post Successfully");
			wait.until(ExpectedConditions.visibilityOf(findElement(driver, "id", "")));
			break;
		case opt11:
			mouseHover(driver, campaign);
			idea.click();
			Thread.sleep(1000);
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Post Successfully");
			wait.until(ExpectedConditions.visibilityOf(findElement(driver, "id", "")));
			break;
		case opt12:
			mouseHover(driver, recognize);
			recognize.click();
			Thread.sleep(1000);
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Post Successfully");
			wait.until(ExpectedConditions.visibilityOf(findElement(driver, "id", "")));
			break;
		case opt13:
			mouseHover(driver, opportunity);
			opportunity.click();
			Thread.sleep(1000);
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Post Successfully");
			wait.until(ExpectedConditions.visibilityOf(findElement(driver, "id", "")));
			break;
		case opt14:
			mouseHover(driver, wikis);
			wikis.click();
			Thread.sleep(1000);
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Wikis Successfully");
			explicitWait(driver, "class", "wikiTitle");
			String wikiTitle = "Test Automation Wiki " + System.currentTimeMillis();
			findElement(driver, "class", "wikiTitle").sendKeys(wikiTitle);
			Thread.sleep(1000);
			ExtentTestManager.getTest().log(Status.PASS, "Entered the Title of the Wiki as: " + wikiTitle);
			WebElement wikiToTeam = findElement(driver, "id", "wikiToTeam");
			wikiToTeam.sendKeys(prop.getProp("wikiToTeam"));
			Thread.sleep(3000);
			wikiToTeam.sendKeys(Keys.ENTER);
			ExtentTestManager.getTest().log(Status.PASS, "Entered the Team Name to send Wiki To");
			findElement(driver, "xpath", "wikiTemplateSelect").click();
			ExtentTestManager.getTest().log(Status.PASS, "Selected Desire Template for Wiki");
			findElement(driver, "xpath", "createWikiButton").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on create Wiki Button");
			Thread.sleep(4000);
			mouseHover(driver, findElement(driver, "xpath", "wikiPublishButton"));
			findElement(driver, "xpath", "wikiPublishButton").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Publish Wiki Button");
			Thread.sleep(2000);
			break;
		case opt15:
			mouseHover(driver, form);
			form.click();
			Thread.sleep(1000);
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Post Successfully");
			wait.until(ExpectedConditions.visibilityOf(findElement(driver, "id", "")));
			break;
		case opt16:
			mouseHover(driver, reminder);
			reminder.click();
			Thread.sleep(1000);
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Post Successfully");
			wait.until(ExpectedConditions.visibilityOf(findElement(driver, "id", "")));
			break;
		case opt17:
			mouseHover(driver, media);
			media.click();
			Thread.sleep(1000);
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Post Successfully");
			wait.until(ExpectedConditions.visibilityOf(findElement(driver, "id", "")));
			break;
		case opt18:
			mouseHover(driver, note);
			note.click();
			Thread.sleep(1000);
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Post Successfully");
			wait.until(ExpectedConditions.visibilityOf(findElement(driver, "id", "")));
			break;
		case opt19:
			mouseHover(driver, greeting);
			greeting.click();
			Thread.sleep(1000);
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Post Successfully");
			wait.until(ExpectedConditions.visibilityOf(findElement(driver, "id", "")));
			break;
		case opt20:
			mouseHover(driver, screenShare);
			screenShare.click();
			Thread.sleep(1000);
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Post Successfully");
			wait.until(ExpectedConditions.visibilityOf(findElement(driver, "id", "")));
			break;
		case opt21:
			mouseHover(driver, ballot);
			ballot.click();
			Thread.sleep(1000);
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Post Successfully");
			wait.until(ExpectedConditions.visibilityOf(findElement(driver, "id", "")));
			break;
		default:
			ExtentTestManager.getTest().log(Status.FAIL, "Please provide valid ActionItem for compose Box");
			break;
		}

	}

	public static void sendEmail(String from, String tos[], String subject, String text) throws MessagingException {
		// Get the session object
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("mangorobot12@gmail.com", "Mango@05");
				// "satheeshg@mangospring.com",
				// "mango@05");// change accordingly
			}
		});
		// compose message
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));// change accordingly
			for (String to : tos) {
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			}
			/*
			 * for (String cc : ccs) message.addRecipient(Message.RecipientType.CC,new
			 * InternetAddress(cc));
			 */
			message.setSubject(subject);
			// Option 1: To send normal text message
			message.setText(text);
			// Option 2: Send the actual HTML message, as big as you like
			// message.setContent("<h1>This is actual message</h1></br></hr>" +
			// text, "text/html");
			// Set the attachment path
			String filename = ReusableAssets.dir + "\\src\\main\\java\\utilities\\profilePic.jpg";
			BodyPart objMessageBodyPart = new MimeBodyPart();
			// Option 3: Send text along with attachment
			objMessageBodyPart.setContent(
					"<h1>Test Mail from Selenium Project for Incoming EMail Test!</h1></br>" + text, "text/html");
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(objMessageBodyPart);
			objMessageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(filename);
			objMessageBodyPart.setDataHandler(new DataHandler(source));
			objMessageBodyPart.setFileName(filename);
			multipart.addBodyPart(objMessageBodyPart);
			message.setContent(multipart);
			// send message
			Transport.send(message);
			System.out.println("message sent successfully");
		} catch (MessagingException e) {
			System.out.println(e.toString());
			;
		}
	}

	//\\TestReport\\MangoApps-Automaton-Report.html
	public static void sendFinaReportEmail(String from, String tos[], int totalpass, int totalfail,int totalskip) throws MessagingException {
		// Get the session object
		String text="Please download and view attached html report for more details of the above results";
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("mangorobot12@gmail.com", "Mango@05");
				// "satheeshg@mangospring.com",
				// "mango@05");// change accordingly
			}
		});
		// compose message
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));// change accordingly
			for (String to : tos) {
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			}
			/*
			 * for (String cc : ccs) message.addRecipient(Message.RecipientType.CC,new
			 * InternetAddress(cc));
			 */
			message.setSubject("Automation Report: Mango-Apps Sanity Points Automation");
			// Option 1: To send normal text message
			message.setText(text);
			// Option 2: Send the actual HTML message, as big as you like
			// message.setContent("<h1>This is actual message</h1></br></hr>" +
			// text, "text/html");
			// Set the attachment path
			String filename = ReusableAssets.dir + "\\TestReport\\MangoApps-Automaton-Report.html";
			BodyPart objMessageBodyPart = new MimeBodyPart();
			// Option 3: Send text along with attachment
			//String htmlValue="<thead bgcolor='#3c1d6b'><tr><th><font color=\"#ffffff\">Feature Name</font></th><th><font color=\"#ffffff\">Planned count</font></th><th bgcolor='limegreen'><font color=\"#ffffff\">Pass Count</font></th><th bgcolor='limegreen'><font color=\"#ffffff\">% Passed</font></th><th bgcolor='red'><font color=\"#ffffff\">Fail Count</font></th><th bgcolor='red'><font color=\"#ffffff\">% Failed</font></th><th bgcolor='darkkhaki'><font color=\"#ffffff\">Skipped Count</font></th><th bgcolor='darkkhaki'><font color=\"#ffffff\">% Skipped</font></th></tr></thead>";
			int totalpassfail = totalpass+totalfail+totalskip;

			//double totalpasspercentage = ((double)totalpass*100)/(double)totalpassfail;
			//double totalfailpercentage = ((double)totalfail*100)/(double)totalpassfail;
			//double totalskippedpercentage = ((double)totalskip*100)/(double)totalpassfail;
			
			//htmlValue=htmlValue.concat("<tr style='background-color:#999999'><td><b>Sanity Suite</td><td><b>"+String.valueOf(totalpassfail)+"</td><td><b>"+String.valueOf(totalpass)+"</td><td><b>"+String.format("%.2f%%",totalpasspercentage)+"</td><td><b>"+String.valueOf(totalfail)+"</td><td><b>"+String.format("%.2f%%",totalfailpercentage)+"</td><td><b>"+String.valueOf(totalskip)+"</td><td><b>"+String.format("%.2f%%",totalskippedpercentage)+"</td></tr>");
			String domainRun="Script Domain URL: <b>"+prop.getProp("newDomainURL");
			String dataCenter1="Data Center of Domain: <b>"+FunctionLibrary.dataCenter;
			String resultTotal="Total count is: ";
			String resultPass="Pass count is: ";
			String resultFail="Failed count is: ";
			String resultSkip="Skipped count is: ";
			String result=resultTotal+"<b>"+String.valueOf(totalpassfail)+"</b><br>"+resultPass+"<b>"+String.valueOf(totalpass)+"</b><br>"+resultFail+"<b>"+String.valueOf(totalfail)+"</b><br>"+resultSkip+"<b>"+String.valueOf(totalskip)+"</b>";
			//objMessageBodyPart.setContent("<h3>Mango-Apps Sanity Points Automation Test Results:</h3>"+htmlValue+"</br><br><br>" + text, "text/html");
			objMessageBodyPart.setContent("<h3>Sanity Points Automation Test Run Results:</h3>"+domainRun+"</b><br>"+dataCenter1+"</b><br><br>"+result+"</br><br><br>" + text, "text/html");
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(objMessageBodyPart);
			objMessageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(filename);
			objMessageBodyPart.setDataHandler(new DataHandler(source));
			objMessageBodyPart.setFileName(filename);
			multipart.addBodyPart(objMessageBodyPart);
			message.setContent(multipart);
			// send message
			Transport.send(message);
			for (String to : tos) {
			System.out.println("Final report is successfully sent to "+to);
			}
		} catch (MessagingException e) {
			System.out.println(e.toString());
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}
	public static void readEmail(String message) {
		Folder folder = null;
		Store store = null;
		System.out.println("***READING MAILBOX...");
		try {
			Properties props = new Properties();
			props.put("mail.store.protocol", "imaps");
			Session session = Session.getInstance(props);
			store = session.getStore("imaps");
			store.connect("imap.gmail.com", "mangorobot12@gmail.com", "Mango@05");
			folder = store.getFolder("INBOX");
			folder.open(Folder.READ_ONLY);
			Message[] messages = folder.getMessages();
			System.out.println("No of Messages : " + folder.getMessageCount());
			System.out.println("No of Unread Messages : " + folder.getUnreadMessageCount());
			for (int i = 0; i < messages.length; i++) {
				System.out.println("Reading MESSAGE # " + (i + 1) + "...");
				Message msg = messages[i];
				if (!msg.getFlags().contains(Flags.Flag.SEEN)) {
					String strMailSubject = "";
					// strMailBody = "";
					// Getting mail subject
					Object subject = msg.getSubject();
					Date receivedDate = msg.getReceivedDate();
					// Getting mail body
					// Object content = getTextFromMessage(msg);
					// Casting objects of mail subject and body into String

					strMailSubject = (String) subject.toString();
					// strMailBody = (String) content.toString();
					// ---- This is what you want to do------
					if (strMailSubject.contains(message)) {
						System.out.println(strMailSubject);
						ExtentTestManager.getTest().log(Status.PASS, "Successfully Received the Outgoing email");
						ExtentTestManager.getTest().log(Status.PASS, "Subject of the mail is: " + strMailSubject);
						ExtentTestManager.getTest().log(Status.PASS, "Mail was Received on: " + receivedDate);
						break;
					}
				}
			}
		} catch (MessagingException messagingException) {
			messagingException.printStackTrace();
		} finally {
			if (folder != null) {
				try {
					folder.close(true);
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (store != null) {
				try {
					store.close();
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static void openNewTab(WebDriver driver) {
		((JavascriptExecutor) driver).executeScript("window.open()");
	}

	public String onPaste() throws UnsupportedFlavorException, IOException{
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		String result = (String) clipboard.getData(DataFlavor.stringFlavor);
		System.out.println("String from Clipboard:" + result);
		return result;
	}
}
