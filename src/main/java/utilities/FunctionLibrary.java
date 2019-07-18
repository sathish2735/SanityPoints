package utilities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.Status;

import io.github.bonigarcia.wdm.ChromeDriverManager;

public class FunctionLibrary extends ReusableAssets {
	public static GetProperty prop = new GetProperty();
	public static SendEmail myEmail = new SendEmail();
	public static WebDriver driver;
	public static WebDriverWait wait;
	public String domainURL;
	public String expectedProjectName;
	public static String browser;
	public String postURL = "";
	public String wikiURL = "";
	public String pageURL = "";
	public String projectMailAddress = "";
	public String dateName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
	public static String dataCenter="";
	
	public void driverSetup(String browserName) {
		try {
			if (browserName.equalsIgnoreCase("chrome")) {
				System.setProperty("webdriver.chrome.driver", dir + prop.getProp("chromeDriver"));
				driver = new ChromeDriver();
				System.out.println("initiated Chrome browser");
				driver.manage().timeouts().implicitlyWait(7, TimeUnit.SECONDS);
				wait = new WebDriverWait(driver, 25);
			} else if (browserName.equalsIgnoreCase("firefox")) {
				System.setProperty("webdriver.gecko.driver", dir + prop.getProp("geckoDriver"));
				driver = new FirefoxDriver();
				System.out.println("initiated Firefox browser");
				// firefox code
			} else if (browserName.equals("IE")) {
				// IE code
				System.setProperty("webdriver.ie.driver", dir + prop.getProp("IEDriver"));
				driver = new InternetExplorerDriver();
				System.out.println("initiated Internet Explorer browser");
			} else if (browserName.equals("headlessChrome")) {
				System.setProperty("webdriver.chrome.driver", dir + prop.getProp("chromeDriver"));
				ChromeOptions chromeOptions = new ChromeOptions();
				chromeOptions.addArguments("--headless");
				driver = new ChromeDriver(chromeOptions);
				driver.manage().timeouts().implicitlyWait(7, TimeUnit.SECONDS);
				wait = new WebDriverWait(driver, 25);
			} else if (browserName.equals("HTMLUnit")) {
				driver = new HtmlUnitDriver();
			} else {
				System.out.println("browser name is not valid");
			}

			driver.manage().window().maximize();
			domainURL = prop.getProp("newDomainURL");
			browser = prop.getProp("browser");
			driver.get(domainURL);
		} catch (Exception e) {
			System.out.println("Error in Driver Setup is: " + e.toString());
		}
	}

	public void getURL(String locatorName) throws IOException {
		driver.get(domainURL + prop.getProp(locatorName));
		ExtentTestManager.getTest().log(Status.INFO, "Entered " + locatorName + " successfully");
	}

	public boolean domainSignUpCheck(String URL) throws IOException {
		boolean functionVerify = false;
		try {
			getURL(URL);
			explicitWait(driver, "xpath", "freeTrial");
			findElement(driver, "xpath", "freeTrial").click();
			ExtentTestManager.getTest().log(Status.INFO, "Clicked on freeTrial successfully");
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fields_for_us_datacenter")));
			ExtentTestManager.getTest().log(Status.INFO, "waited until the popup comes in");
			// findElement(driver, "id", "dataCenter").click();
			// Need to check how to automate this drop down
			WebElement dataCenterFields = findElement(driver, "id", "dataCenterFields");
			ExtentTestManager.getTest().log(Status.INFO,
					"Limited the scope of WebDriver in order to identify email uniquely");
			dataCenterFields.findElement(By.id("email")).sendKeys(prop.getProp("emailForSignUP"));
			ExtentTestManager.getTest().log(Status.INFO, "Entered email address successfully");
			findElement(driver, "id", "firstName").sendKeys(prop.getProp("firstName"));
			ExtentTestManager.getTest().log(Status.INFO, "Entered firstName successfully");
			findElement(driver, "id", "lastName").sendKeys(prop.getProp("lastName"));
			ExtentTestManager.getTest().log(Status.INFO, "Entered lastName successfully");
			// Write code here to skip captcha
			findElement(driver, "id", "signUpButton").click();
			ExtentTestManager.getTest().log(Status.INFO, "Clicked on SignUP button successfully");
			explicitWait(driver, "xpath", "loginToDomain");
			functionVerify = true;
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.INFO, "Error in try block " + e.toString());
		}
		return functionVerify;
	}

	public boolean domainLoginCheck() {
		boolean functionVerify = false;
		try {
			// Need to remove this once the captcha issue is resolved
			driver.get(prop.getProp("tempFixURL"));
			String parentWindow = driver.getWindowHandle();
			findElement(driver, "xpath", "loginToDomain").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Login to Domain button successfully");
			Set<String> winHandles = driver.getWindowHandles();
			// Loop through all handles
			for (String handle : winHandles) {
				if (!handle.equals(parentWindow)) {
					driver.switchTo().window(handle);
				}
			}
			// email id will be auto populated
			findElement(driver, "id", "password").sendKeys(prop.getProp("QAGodPassword"));
			ExtentTestManager.getTest().log(Status.PASS, "entered the password successfully");
			findElement(driver, "id", "loginButton").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Login button");
			explicitWait(driver, "id", "globalSearchBox");
			ExtentTestManager.getTest().log(Status.PASS, "Waiting for the URL to get redirected to user overview");
			functionVerify = true;
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "Error in try block " + e.toString());
		}
		return functionVerify;

	}

	public boolean defaultGroupsCheck() {
		boolean functionVerify = false;
		try {
			getURL("groupsTabURL");
			ExtentTestManager.getTest().log(Status.INFO, "Clicked on groups tab successfully");
			Thread.sleep(2000);
			List<String> defaultGroups = Arrays.asList("Intranet Admins", "Everyone", "Domain Admins",
					"Idea Management Team");
			for (int i = 1; i < 5; i++) {
				String locatorName = "//*[@id='project_description']/div[" + i + "]/a/div/div[2]/p[1]/span/span";
				String groupName = driver.findElement(By.xpath(locatorName)).getText();
				if (defaultGroups.contains(groupName)) {
					ExtentTestManager.getTest().log(Status.PASS, groupName + " group is present for this domain");
				}
			}
			functionVerify = true;
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.INFO, "Error in try block " + e.toString());
		}
		return functionVerify;
	}

	public boolean EveryoneAddedCheck() {
		// This methods adds some new users in the domain and checks if the system is by
		// default adding
		// them to Everyone group
		boolean functionVerify = false;
		try {
			getURL("peopleTabURL");
			ExtentTestManager.getTest().log(Status.PASS, "Entered into People Module");
			wait.until(ExpectedConditions.visibilityOf(findElement(driver, "xpath", "tools")));
			findElement(driver, "xpath", "tools").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Tools dropdown");
			mouseHover(driver, findElement(driver, "xpath", "invitePeople"));
			findElement(driver, "xpath", "invitePeople").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Invite People Option");
			for (int i = 1; i < 4; i++) {
				findElement(driver, "id", "guestFirstName").sendKeys(prop.getProp("User" + i + "FirstName"));
				ExtentTestManager.getTest().log(Status.PASS, "Entered Guest User " + i + " firstName successfully");
				findElement(driver, "id", "guestLastName").sendKeys(prop.getProp("User" + i + "LastName"));
				ExtentTestManager.getTest().log(Status.PASS, "Entered Guest User " + i + " lastName successfully");
				findElement(driver, "id", "guestEmail").sendKeys(prop.getProp("User" + i + "Email"));
				ExtentTestManager.getTest().log(Status.PASS, "Entered Guest User " + i + " Email address successfully");
				findElement(driver, "id", "addButton").click();
				ExtentTestManager.getTest().log(Status.PASS, "all details of User " + i + " has been added");
			}
			WebElement sendInvite = findElement(driver, "id", "sendInviteNow");
			scrollToView(driver, sendInvite);
			if (sendInvite.isEnabled()) {
				sendInvite.click();
				ExtentTestManager.getTest().log(Status.PASS, "Invitation is sent to all added users");
				functionVerify = true;
			} else {
				ExtentTestManager.getTest().log(Status.FAIL,
						"Send invite is not enabled, Please check if user details added properly");
			}
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "Error in try block " + e.toString());
		}
		return functionVerify;
	}

	public boolean verifyUsersInEveryoneCheck() {
		boolean functionVerify = false;
		try {
			getURL("groupsTabURL");
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Groups Module");
			explicitWait(driver, "partialLinkText", "groupEveryone");
			findElement(driver, "partialLinkText", "groupEveryone").click();
			ExtentTestManager.getTest().log(Status.PASS, "Selected Everyone group");
			explicitWait(driver, "linkText", "membersEveryone");
			findElement(driver, "linkText", "membersEveryone").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Members tab of this group");
			WebElement allMemberCount = findElement(driver, "id", "allMemberCount");
			if (allMemberCount.getText().contains(prop.getProp("totalMemberCount"))) {
				ExtentTestManager.getTest().log(Status.PASS, "all Guest users have been added to Everyone group");
				functionVerify = true;
			} else {
				ExtentTestManager.getTest().log(Status.FAIL,
						"There is Count mismatch in everyone group: Please cross check again");
			}
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "Error in try block " + e.toString());
		}
		return functionVerify;
	}

	public boolean timeZoneSetupCheck() {
		boolean functionVerify = false;
		try {
			getURL("domainLocaleURL");
			Select myTimeZone = new Select(findElement(driver, "id", "defaultTimeZone"));
			myTimeZone.selectByValue(prop.getProp("timeZoneNeeded"));
			findElement(driver, "xpath", "LocaleSettingsapplyToAll").click();
			Thread.sleep(1000);
			findElement(driver, "id", "saveLocaleSettings").click();
			findElement(driver, "linkText", "applySettingAlert").click();
			ExtentTestManager.getTest().log(Status.PASS, "Passed sampleTest Successfully");
			functionVerify = true;
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "Error in try block " + e.toString());
		}
		return functionVerify;
	}

	public boolean askQuestionCheck() {
		boolean functionVerify = false;
		try {
			getURL("expectedDefaultLandingURL");
			composeClick(driver, wait, "QUESTION");
			ExtentTestManager.getTest().log(Status.PASS, "Posted question");
			functionVerify = true;
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "Error in try block " + e.toString());
			ExtentTestManager.getTest().log(Status.FAIL, "Error occured at: " + e.getStackTrace());
			driver.quit();
			driverSetup(browser);
			loginCheck();

		}
		return functionVerify;
	}

	public boolean changeDPCheck() {
		boolean functionVerify = false;
		try {
			userOptions(driver, "viewMyProfile");
			explicitWait(driver, "id", "uploadedImage");
			findElement(driver, "id", "uploadedImage").click();
			ExtentTestManager.getTest().log(Status.INFO, "Clicked on Change Photo Option");
			explicitWait(driver, "id", "changePhotoButton");
			findElement(driver, "id", "changePhotoButton").click();
			ExtentTestManager.getTest().log(Status.INFO, "Clicked on Change Photo Button");
			Thread.sleep(2000);
			upload(driver, "profilePicPath");
			findElement(driver, "id", "applyChanges").click();
			ExtentTestManager.getTest().log(Status.INFO, "Clicked on Apply Changes Button");
			explicitWait(driver, "id", "uploadedImage");
			ExtentTestManager.getTest().log(Status.PASS, "Successfully changed the DP of the user");
			functionVerify = true;
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "Error in try block " + e.toString());
		}
		return functionVerify;
	}

	public boolean defaultLandingPageSettingCheck() {
		boolean functionVerify = false;
		try {
			getURL("landingPageSetupURL");
			Select defaultLandingPage = new Select(findElement(driver, "id", "landingPageDropdown"));
			defaultLandingPage.selectByVisibleText(prop.getProp("desiredPage"));
			ExtentTestManager.getTest().log(Status.PASS, "Selected the Landing page as " + prop.getProp("desiredPage"));
			findElement(driver, "xpath", "applySettingsToAll").click();
			ExtentTestManager.getTest().log(Status.INFO,
					"Selected the option to apply this settings to Existing & New users");
			findElement(driver, "id", "saveLandingPage").click();
			ExtentTestManager.getTest().log(Status.INFO, "Clicked on Save button");
			explicitWait(driver, "linkText", "applySettingAlert");
			findElement(driver, "linkText", "applySettingAlert").click();
			ExtentTestManager.getTest().log(Status.PASS, "Handled the alert by clicking on applySettings");
			userOptions(driver, "goToUserPortal");
			ExtentTestManager.getTest().log(Status.PASS, "Came back into User Portal");
			/*
			 * explicitWait(driver, "id", "globalSearchBox"); userOptions(driver,
			 * "signOut"); ExtentTestManager.getTest().log(Status.PASS,
			 * "Signed Out of the application from User Portal"); explicitWait(driver, "id",
			 * "userID"); loginCheck(); ExtentTestManager.getTest().log(Status.PASS,
			 * "Logged in again successfully");
			 */
			explicitWait(driver, "id", "globalSearchBox");
			Thread.sleep(2000);
			if (driver.getCurrentUrl().contains(prop.getProp("expectedDefaultLandingURL"))) {
				ExtentTestManager.getTest().log(Status.PASS, "Default Landing page has been verified successfully");
				functionVerify = true;
			} else {
				ExtentTestManager.getTest().log(Status.FAIL,
						"Landing Page URL is not matching with expected one: Please Check");
			}

		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "Error in try block " + e.toString());
			driver.quit();
			driverSetup(browser);
			loginCheck();
		}
		return functionVerify;
	}

	public boolean loginCheck() {
		boolean functionVerify = false;
		try {
			ExtentTestManager.getTest().log(Status.PASS, "Running on domain: " + domainURL);
			WebElement userID=findElement(driver, "id", "userID");
			userID.clear();
			Thread.sleep(1000);
			userID.sendKeys(prop.getProp("emailForSignUP"));
			ExtentTestManager.getTest().log(Status.PASS, "Entered Username Successfully");
			WebElement password=findElement(driver, "id", "password");
			password.clear();
			Thread.sleep(1000);
			password.sendKeys(prop.getProp("GodPassword"));
			ExtentTestManager.getTest().log(Status.PASS, "Entered password Successfully");
			WebElement loginButton=findElement(driver, "id", "loginButton");
			loginButton.click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Login Button Successfully");
			explicitWait(driver, "id", "globalSearchBox");
			WebElement notify = driver.findElement(By.xpath("//*[@id=\"notice\"]/a"));
			try {
				notify.click();
				ExtentTestManager.getTest().log(Status.INFO, "Closed the Notification bar");
			} catch (Exception e) {
				ExtentTestManager.getTest().log(Status.INFO, "Pop-out is not shown up/not handled properly");
			}
			functionVerify = true;
			ExtentTestManager.getTest().log(Status.PASS, "Logged In Successfully");
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "Error in try block " + e.toString());
			driver.navigate().refresh();
			loginCheck();
		}
		return functionVerify;
	}
	public void dataCenter() {
		try {
			getURL("adminHomeTabURL");
			dataCenter=driver.findElement(By.xpath("//div[contains(text(),'Data Center:')]/following-sibling::div")).getText();
			ExtentTestManager.getTest().log(Status.PASS, "Data center value is collected into a String");
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "Error in try block of Data Center " + e.toString());
		}
	}
	
	public boolean logoutCheck() {
		boolean functionVerify = false;
		try {
			Thread.sleep(1000);
			userOptions(driver, "signOut");
			ExtentTestManager.getTest().log(Status.PASS, "Successfully logged out of the application");
			functionVerify = true;
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "Error in try block " + e.toString());
		}
		return functionVerify;
	}

	public boolean projectCreationCheck() {
		boolean functionVerify = false;
		try {
			getURL("projectTabURL");
			ExtentTestManager.getTest().log(Status.PASS, "Entered into projects Module successfully");
			Thread.sleep(2000);
			List<WebElement> teamCount = driver.findElements(By.xpath("//div[contains(@id, 'teamBox')]"));
			int rowCount = teamCount.size();
			List<String> projectList = new ArrayList<String>();
			for (int i = 1; i <= rowCount; i++) {
				String projectLocatorName = "//*[@id='project_description']/div[" + i + "]/a/div/div[2]/p[1]/span/span";
				projectList.add(driver.findElement(By.xpath(projectLocatorName)).getText());
			}
			expectedProjectName = prop.getProp("ProjectName");
			if (!projectList.contains(expectedProjectName)) {
				findElement(driver, "linkText", "createProject").click();
				ExtentTestManager.getTest().log(Status.PASS, "Clicked on Create Project Button");
				explicitWait(driver, "id", "projectName");
				findElement(driver, "id", "projectName").sendKeys(expectedProjectName);
				ExtentTestManager.getTest().log(Status.PASS, "Entered Project name as " + expectedProjectName);
				findElement(driver, "id", "shortDescription").sendKeys(prop.getProp("ShortDescription"));
				ExtentTestManager.getTest().log(Status.PASS, "Entered the short Description for the project");
				if (prop.getProp("PermissionLevel").equalsIgnoreCase("Public")) {
					findElement(driver, "xpath", "publicPermission").click();
					ExtentTestManager.getTest().log(Status.PASS, "Selected the Access Permissions to be Public");
				} else if (prop.getProp("PermissionLevel").equalsIgnoreCase("Private")) {
					findElement(driver, "xpath", "privatePermission").click();
					ExtentTestManager.getTest().log(Status.PASS, "Selected the Access Permissions to be Private");
				} else if (prop.getProp("PermissionLevel").equalsIgnoreCase("Secret")) {
					findElement(driver, "xpath", "secretPermission").click();
					ExtentTestManager.getTest().log(Status.PASS, "Selected the Access Permissions to be Secret");
				} else {
					ExtentTestManager.getTest().log(Status.FAIL, "Please provide valid Permission Type");
				}
				if (prop.getProp("defaultMembership").equalsIgnoreCase("Member")) {
					findElement(driver, "xpath", "memberMembership").click();
					ExtentTestManager.getTest().log(Status.PASS,
							"Default membership of each member will be as a Member");
				} else if (prop.getProp("defaultMembership").equalsIgnoreCase("Admin")) {
					findElement(driver, "xpath", "adminMembership").click();
					ExtentTestManager.getTest().log(Status.PASS,
							"Default membership of each member will be as an Admin");
				} else {
					ExtentTestManager.getTest().log(Status.FAIL, "Please provide valid value for default Membership");
				}
				WebElement timeZone = findElement(driver, "id", "projectTimeZone");
				Select selectTimeZone = new Select(timeZone);
				selectTimeZone.selectByValue(prop.getProp("PreferredProjectTimeZone"));
				ExtentTestManager.getTest().log(Status.PASS,
						"Selected the Time Zone as per " + prop.getProp("PreferredProjectTimeZone"));
				findElement(driver, "linkText", "saveNContinue").click();
				ExtentTestManager.getTest().log(Status.PASS, "Clicked on Save& Continue");
				Thread.sleep(2000);
				// Need to enhance code to give option to user to select modules to be enabled
				WebElement Pages = findElement(driver, "xpath", "projectPageModule");
				scrollToView(driver, Pages);
				if (Pages.getText().equalsIgnoreCase("Pages")) {
					driver.findElement(By.xpath("//*[@id=\"enable_overview\"]/div[3]/div/label")).click();
					ExtentTestManager.getTest().log(Status.PASS, "Enabled pages module");
				}
				
				if(findElement(driver, "linkText", "SaveNExit").isDisplayed()) {
					findElement(driver, "linkText", "SaveNExit").click();
					ExtentTestManager.getTest().log(Status.PASS, "Clicked on Save& Exit");
					Thread.sleep(1000);
				}else if(findElement(driver, "xpath", "downArrow").isDisplayed()) {
					findElement(driver, "xpath", "downArrow").click();
					Thread.sleep(1000);
					WebElement finalSave=findElement(driver, "xpath", "saveNGoToProject");
					mouseHover(driver, finalSave);
					finalSave.click();
					ExtentTestManager.getTest().log(Status.PASS, "Clicked on Save& Exit");
					Thread.sleep(1000);
				}
				WebElement actualProjectName = driver
						.findElement(By.xpath("//span[contains(text(), '" + expectedProjectName + "')]"));
				if (actualProjectName.getText().equalsIgnoreCase(expectedProjectName)) {
					ExtentTestManager.getTest().log(Status.PASS,
							expectedProjectName + " Project is created Successfully");
					functionVerify = true;
					actualProjectName.click();
					explicitWait(driver, "xpath", "projectTools");
					findElement(driver, "xpath", "projectTools").click();
					WebElement getProjectLink = findElement(driver, "xpath", "getShareLinkEmail");
					wait.until(ExpectedConditions.visibilityOf(getProjectLink));
					getProjectLink.click();
					WebElement copyEmailID = findElement(driver, "xpath", "copyProjectEmailAddress");
					wait.until(ExpectedConditions.visibilityOf(copyEmailID));
					copyEmailID.click();
					projectMailAddress = onPaste();
					ExtentTestManager.getTest().log(Status.INFO, "URL of the project is: " + projectMailAddress);
					findElement(driver, "xpath", "copyProjectEmailClose").click();
				} else {
					ExtentTestManager.getTest().log(Status.PASS,
							"Project got created with a different Name: Please Check");
				}
			} else {
				ExtentTestManager.getTest().log(Status.FAIL,
						"There is an existing Project with the same name: Please give Unique Name");
			}

		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "Error in try block " + e.toString());
		}
		return functionVerify;
	}

	public boolean createStaticPageCheck() {
		boolean functionVerify = false;
		try {
			getURL("projectTabURL");
			ExtentTestManager.getTest().log(Status.INFO, "Entered into Project Module");
			findElement(driver, "linkText", "createProjectName").click();
			ExtentTestManager.getTest().log(Status.INFO, "Entered into Desired Project");
			explicitWait(driver, "linkText", "projectPagesTab");
			findElement(driver, "linkText", "projectPagesTab").click();
			ExtentTestManager.getTest().log(Status.PASS, "Successfully clicked on Project Pages");
			Thread.sleep(2000);
			findElement(driver, "cssSelector", "projectPageTools").click();
			ExtentTestManager.getTest().log(Status.PASS, "Successfully clicked on Project Page Tools");
			WebElement addNewPage = findElement(driver, "linkText", "newPageOption");
			wait.until(ExpectedConditions.visibilityOf(addNewPage));
			mouseHover(driver, addNewPage);
			addNewPage.click();
			ExtentTestManager.getTest().log(Status.PASS, "Successfully clicked on add new Page");
			explicitWait(driver, "id", "projectPageName");
			String pageTitle = prop.getProp("StaticPageName") + dateName;
			findElement(driver, "id", "projectPageName").sendKeys(pageTitle);
			ExtentTestManager.getTest().log(Status.PASS, "Entered Page Title as: " + pageTitle);
			Thread.sleep(2000);
			findElement(driver, "xpath", "pageTemplateSelect").click();
			findElement(driver, "linkText", "pageNextButton").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Next Button");
			Thread.sleep(500);
			findElement(driver, "linkText", "pageNextButton").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Second Next Button");
			// findElement(driver, "xpath", "pageEditParagraph").clear();
			// findElement(driver, "xpath", "pageEditParagraph").sendKeys("This is to test
			// Static page flow");
			Thread.sleep(500);
			findElement(driver, "id", "pageSaveNPublish").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Publish Button");
			ExtentTestManager.getTest().log(Status.PASS, "Static Page Creation Check is successfull");
			explicitWait(driver, "cssSelector", "projectPageTools");
			Thread.sleep(3000);
			findElement(driver, "cssSelector", "projectPageTools").click();
			ExtentTestManager.getTest().log(Status.PASS, "Again Clicked on Page Tools dropdown");
			explicitWait(driver, "linkText", "editCurrentPage");
			WebElement editPage = findElement(driver, "linkText", "editCurrentPage");
			mouseHover(driver, editPage);
			editPage.click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on edit Page Option");
			explicitWait(driver, "id", "projectPageName");
			findElement(driver, "id", "pageFullName").clear();
			String NewPageName = prop.getProp("NewNameStaticPage") + dateName;
			findElement(driver, "id", "pageFullName").sendKeys(NewPageName);
			ExtentTestManager.getTest().log(Status.PASS, "Changed the Page Name to " + NewPageName);
			Thread.sleep(1000);
			findElement(driver, "linkText", "pageNextButton").click();
			findElement(driver, "linkText", "pageNextButton").click();
			findElement(driver, "id", "pageSaveNPublish").click();
			findElement(driver, "id", "PageSavePublishNow").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Publish Button");
			if (findElement(driver, "id", "sitePageName").getText().equalsIgnoreCase(NewPageName)) {
				ExtentTestManager.getTest().log(Status.PASS,
						"Static Page Creation Check and Edit Check is successfull");
				functionVerify = true;
				Thread.sleep(3000);
				findElement(driver, "cssSelector", "projectPageTools").click();
				explicitWait(driver, "linkText", "copyPageLinkOption");
				findElement(driver, "linkText", "copyPageLinkOption").click();
				Thread.sleep(1000);
				pageURL = onPaste();
			}

		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "Error in try block " + e.toString());
		}
		return functionVerify;
	}

	public boolean createDynamicPageCheck() {
		boolean functionVerify = false;
		try {
			getURL("projectTabURL");
			ExtentTestManager.getTest().log(Status.INFO, "Entered into Project Module");
			findElement(driver, "linkText", "createProjectName").click();
			ExtentTestManager.getTest().log(Status.INFO, "Entered into Desired Project");
			explicitWait(driver, "linkText", "projectPagesTab");
			findElement(driver, "linkText", "projectPagesTab").click();
			ExtentTestManager.getTest().log(Status.PASS, "Successfully clicked on Project Pages");
			Thread.sleep(2000);
			explicitWait(driver, "cssSelector", "projectPageTools");
			findElement(driver, "cssSelector", "projectPageTools").click();
			ExtentTestManager.getTest().log(Status.PASS, "Successfully clicked on Project Page Tools");
			WebElement addNewPage = findElement(driver, "linkText", "newPageOption");
			wait.until(ExpectedConditions.visibilityOf(addNewPage));
			mouseHover(driver, addNewPage);
			addNewPage.click();
			ExtentTestManager.getTest().log(Status.PASS, "Successfully clicked on add new Page");
			explicitWait(driver, "id", "projectPageName");
			String pageTitle = prop.getProp("DynamicPageName") + dateName;
			findElement(driver, "id", "projectPageName").sendKeys(pageTitle);
			ExtentTestManager.getTest().log(Status.PASS, "Entered Page Title as " + pageTitle);
			Thread.sleep(1000);
			findElement(driver, "linkText", "dynamicPageLink").click();
			ExtentTestManager.getTest().log(Status.PASS, "Successfully clicked on Dynamic Page templates");
			Thread.sleep(2000);
			findElement(driver, "xpath", "DynamicPageTemplateSelect").click();
			ExtentTestManager.getTest().log(Status.PASS, "Selected the Dynamic Page Template");
			findElement(driver, "linkText", "pageNextButton").click();
			Thread.sleep(1000);
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Next Button");
			Thread.sleep(1000);
			findElement(driver, "linkText", "pageNextButton").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Second Next Button");
			// findElement(driver, "xpath", "pageEditParagraph").clear();
			// findElement(driver, "xpath", "pageEditParagraph").sendKeys("This is to test
			// Static page flow");
			Thread.sleep(2000);
			findElement(driver, "id", "pageSaveNPublish").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Publish Button");
			ExtentTestManager.getTest().log(Status.PASS, "Dynamic Page Creation Check is successfull");
			explicitWait(driver, "cssSelector", "projectPageTools");
			Thread.sleep(2000);
			findElement(driver, "cssSelector", "projectPageTools").click();
			ExtentTestManager.getTest().log(Status.PASS, "Again Clicked on Page Tools dropdown");
			explicitWait(driver, "linkText", "editCurrentPage");
			WebElement editPage = findElement(driver, "linkText", "editCurrentPage");
			mouseHover(driver, editPage);
			editPage.click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on edit Page Option");
			explicitWait(driver, "id", "projectPageName");
			findElement(driver, "id", "pageFullName").clear();
			String NewPageName = prop.getProp("NewNameDynamicPage") + dateName;
			findElement(driver, "id", "pageFullName").sendKeys(NewPageName);
			ExtentTestManager.getTest().log(Status.PASS, "Changed the Page Name");
			Thread.sleep(1000);
			findElement(driver, "linkText", "pageNextButton").click();
			findElement(driver, "linkText", "pageNextButton").click();
			findElement(driver, "id", "pageSaveNPublish").click();
			findElement(driver, "id", "PageSavePublishNow").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Publish Button");
			Thread.sleep(1000);
			if (findElement(driver, "id", "sitePageName").getText().equalsIgnoreCase(NewPageName)) {
				ExtentTestManager.getTest().log(Status.PASS,
						"Dynamic Page Creation Check and Edit Check is successfull");
				functionVerify = true;
			}
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "Error in try block " + e.toString());
		}
		return functionVerify;
	}

	public boolean createReccurenceTaskCheck() {
		boolean functionVerify = false;
		try {
			getURL("projectTabURL");
			ExtentTestManager.getTest().log(Status.INFO, "Entered into Project Module");
			findElement(driver, "linkText", "createProjectName").click();
			ExtentTestManager.getTest().log(Status.INFO, "Entered into Desired Project");
			findElement(driver, "linkText", "projectTaskTab").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Task Tab of Projects");
			explicitWait(driver, "xpath", "projectTaskTools");
			findElement(driver, "xpath", "projectTaskTools").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on task tools");
			Thread.sleep(2000);
			findElement(driver, "linkText", "addNewTaskOption").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on add new task");
			explicitWait(driver, "id", "projectTaskName");
			Thread.sleep(2000);
			String taskName = prop.getProp("taskName") + dateName;
			findElement(driver, "id", "projectTaskName").sendKeys(taskName);
			ExtentTestManager.getTest().log(Status.PASS, "Entered the new task name as " + taskName);
			Thread.sleep(2000);
			findElement(driver, "class", "recurrinTaskToggleButton").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on reccurance Toggle Button to ON it");
			findElement(driver, "id", "recurringTask").click();
			ExtentTestManager.getTest().log(Status.PASS, "Expanded the fields of Recurrance Task");
			findElement(driver, "id", "everyDays").sendKeys("1");
			ExtentTestManager.getTest().log(Status.PASS, "Entered number of recurrances Needed");
			Thread.sleep(1000);
			findElement(driver, "id", "recurringTask").click();
			ExtentTestManager.getTest().log(Status.PASS, "Minimized the fields of Recurrance Task");
			Thread.sleep(1000);
			scrollToView(driver, findElement(driver, "id", "taskSaveButton"));
			Thread.sleep(1000);
			findElement(driver, "id", "taskSaveButton").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Save Option: Task Created");
			Thread.sleep(2000);
			driver.findElement(By.xpath("//span[text()='" + taskName + "']")).click();
			Thread.sleep(2000);
			if (driver.findElement(By.className("task-right-content")).isDisplayed()) {
				WebElement nextOccurance = findElement(driver, "xpath", "nextOccuranceCheck");
				scrollToView(driver, nextOccurance);
				ExtentTestManager.getTest().log(Status.INFO, nextOccurance.getText());
				if (nextOccurance.getText().contains(dateProvider())) {
					ExtentTestManager.getTest().log(Status.PASS,
							"Verified the Recurrance Date Successfully and its matching with tomorrow's Date");
					functionVerify = true;
				}
			} else {
				ExtentTestManager.getTest().log(Status.INFO,
						"Task Content is not Opened, Clicking again to open Task Details");
				driver.findElement(By.xpath("//span[text()='" + taskName + "']")).click();
				Thread.sleep(1000);
				WebElement nextOccurance = findElement(driver, "xpath", "nextOccuranceCheck");
				scrollToView(driver, nextOccurance);
				ExtentTestManager.getTest().log(Status.INFO, nextOccurance.getText());
				if (nextOccurance.getText().contains(dateProvider())) {
					ExtentTestManager.getTest().log(Status.PASS,
							"Verified the Recurrance Date Successfully and its matching with tomorrow's Date");
					functionVerify = true;
				}
			}
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "Error in try block " + e.toString());
			findElement(driver, "xpath", "taskDialogueCloseButton").click();
		}
		return functionVerify;
	}

	public boolean createPostFromComposeCheck() {
		boolean functionVerify = false;
		try {
			driver.get(domainURL);
			composeClick(driver, wait, "POST");
			Thread.sleep(6000);
			if (driver.getCurrentUrl().contains("posts")) {
				WebElement actualPost=driver.findElement(By.xpath("//span[contains(text(),'" + expectedPostTitle + "')]"));
				if (actualPost.isDisplayed()) {
					//scrollToView(driver, actualPost);
					driver.findElement(By.xpath("//span[contains(text(),'" + expectedPostTitle + "')]")).click();
					explicitWait(driver, "xpath", "postToolsButton");
					findElement(driver, "xpath", "postToolsButton").click();
					explicitWait(driver, "linkText", "copyPostLinkOption");
					findElement(driver, "linkText", "copyPostLinkOption").click();
					Thread.sleep(1000);
					postURL = onPaste();
					findElement(driver, "xpath", "closePostButton").click();
					ExtentTestManager.getTest().log(Status.PASS, "Post has been created and posted Successfully");
				}
			}
			functionVerify = true;
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "Error in try block " + e.toString());
			driver.quit();
			driverSetup(browser);
			loginCheck();
		}
		return functionVerify;
	}

	public boolean createWikiFromComposeCheck() {
		boolean functionVerify = false;
		try {
			driver.get(domainURL);
			composeClick(driver, wait, "WIKIS");
			Thread.sleep(2000);
			WebElement wikiTools = findElement(driver, "xpath", "postToolsButton");
			wikiTools.click();
			explicitWait(driver, "linkText", "copyWikiLinkOption");
			findElement(driver, "linkText", "copyWikiLinkOption").click();
			Thread.sleep(1000);
			wikiURL = onPaste();
			functionVerify = true;
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "Error in try block " + e.toString());
			driver.quit();
			driverSetup(browser);
			loginCheck();
		}
		return functionVerify;
	}

	public boolean documentConversionCheck() {
		boolean functionVerify = false;
		try {
			uploadFuntionality("textFilePath");
			findElement(driver, "xpath", "addedFileinFolder").click();
			ExtentTestManager.getTest().log(Status.PASS, "Opening the file that Just got uploaded");
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(text(), 'this is to test')]")));
			if (findElement(driver, "xpath", "textfromFile").getText()
					.equalsIgnoreCase(prop.getProp("textFileContent"))) {
				ExtentTestManager.getTest().log(Status.PASS,
						"Uploaded document Name is: " + findElement(driver, "xpath", "uploadedFileName").getText());
				ExtentTestManager.getTest().log(Status.PASS,
						"Inner text of the document is: " + findElement(driver, "xpath", "textfromFile").getText());
				ExtentTestManager.getTest().log(Status.PASS, "Text Document conversion is Successfull");
				functionVerify = true;
			} else {
				ExtentTestManager.getTest().log(Status.FAIL,
						"Either the document was opened lately/it didn't open preview at all/Inner text is not matching: Please check");
			}
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "Error in try block " + e.toString());
			//findElement(driver, "id", "uploadedFileCloseButton").click();
		}
		return functionVerify;
	}

	public boolean fileUploadNewVersionCheck() {
		boolean functionVerify = false;
		try {
			findElement(driver, "xpath", "fileAdvancedTools").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Advanced Tools drop down");
			Thread.sleep(1000);
			WebElement uploadNewVersion = findElement(driver, "class", "uploadNewVersion");
			mouseHover(driver, uploadNewVersion);
			uploadNewVersion.click();
			ExtentTestManager.getTest().log(Status.PASS, "Selected Upload New Version Option");
			Thread.sleep(1000);
			findElement(driver, "id", "addFilesButton").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on add files button");
			Thread.sleep(1000);
			upload(driver, "textFileNextVersionPath");
			Thread.sleep(3000);
			driver.navigate().refresh();
			ExtentTestManager.getTest().log(Status.PASS, "Refreshed the current Page");
			findElement(driver, "xpath", "addedFileinFolder").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on the New version of the file");
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(text(), 'this is to test')]")));
			ExtentTestManager.getTest().log(Status.PASS,
					"Inner text of the document is: " + findElement(driver, "xpath", "textfromFile").getText());
			if (findElement(driver, "xpath", "textfromFile").getText()
					.equalsIgnoreCase(prop.getProp("nextVersionFileContent"))) {
				ExtentTestManager.getTest().log(Status.PASS,
						"Uploaded document Name is: " + findElement(driver, "xpath", "uploadedFileName").getText());

				ExtentTestManager.getTest().log(Status.PASS, "Text Document conversion is Successfull");
			} else {
				ExtentTestManager.getTest().log(Status.FAIL,
						"Either the document was opened lately or it didn't open the proper text preview at all: Please check");
			}
			findElement(driver, "id", "uploadedFileCloseButton").click();
			ExtentTestManager.getTest().log(Status.INFO, "Closed the Preview window");
			functionVerify = true;
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.INFO, e.toString());
		}
		return functionVerify;
	}

	public boolean videoUploadNConversionCheck() {
		boolean functionVerify = false;
		try {
			uploadFuntionality("VideoFilePath");
			findElement(driver, "xpath", "addedVideoinFolder").click();
			ExtentTestManager.getTest().log(Status.PASS, "Opening the Video file that just got uploaded");
			wait.until(
					ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"mediaspace_preview\"]/div/video")));
			if (findElement(driver, "xpath", "videoFileExists").isDisplayed()) {
				Thread.sleep(5000);
				ExtentTestManager.getTest().log(Status.PASS,
						"Uploaded Video Name is: " + findElement(driver, "xpath", "uploadedFileName").getText());
				ExtentTestManager.getTest().log(Status.PASS, "Video Conversion is Successful");
				findElement(driver, "id", "uploadedFileCloseButton").click();
				ExtentTestManager.getTest().log(Status.INFO, "Closed the Preview window");
				functionVerify = true;
			} else {
				ExtentTestManager.getTest().log(Status.PASS,
						"Video preview is not successful: Video is not shown here");
			}
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "Error in try block " + e.toString());
		}
		return functionVerify;
	}

	public boolean incomingEmailwithTeamIDCheck() {
		boolean functionVerify = false;
		try {
			// ,"satheesh.tech5group@hubmail.mangoapps.com",
			// "automationtest.tech5group@hubmail.mangoapps.com"
			String to[] = { projectMailAddress };
			sendEmail("mangorobot12@gmail.com", to, "this is to test email functionality", "email test");
			// sendEmail("satheeshg@mangospring.com", to, "this is to test email
			// functionality", "email test");
			ExtentTestManager.getTest().log(Status.PASS, "Email is sent from external mail system to application");
			getURL("newsfeedTabURL");
			wait.until(ExpectedConditions.elementToBeClickable(By.id("new_updates_notification_cont")));
			// findElement(driver, "id", "newUpdatePush").click();
			getURL("newsfeedTabURL");
			Thread.sleep(1000);
			if (findElement(driver, "xpath", "mailSubject").isDisplayed()) {
				ExtentTestManager.getTest().log(Status.PASS, "Email is Recieved by application successfully");
				findElement(driver, "xpath", "mailSubject").click();
				Thread.sleep(3000);
			}
			functionVerify = true;
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "Error in try block " + e.toString());
		}
		return functionVerify;
	}

	public boolean incomingEmailReplyViaShareCheck() {
		boolean functionVerify = false;
		try {
			WebElement mailPopUp = findElement(driver, "class", "mailPopUp");
			mailPopUp.findElement(By.xpath("//a[contains(@id,'share_actions_el')]")).click();
			Thread.sleep(1000);
			WebElement shareOverEmail = mailPopUp.findElement(By.xpath("//a[@title=\"Share over Email\"]"));
			mouseHover(driver, shareOverEmail);
			shareOverEmail.click();
			explicitWait(driver, "id", "toAddressForSendingEmail");
			findElement(driver, "id", "toAddressForSendingEmail").sendKeys("mangorobot12@gmail.com");
			Thread.sleep(1000);
			findElement(driver, "id", "sendingEmailSubject").clear();
			findElement(driver, "id", "sendingEmailSubject").sendKeys("This is to check Outgoing Email Functionality");
			Thread.sleep(2000);
			findElement(driver, "xpath", "sendEmailButton").click();
			Thread.sleep(2000);
			WebElement commentSection = findElement(driver, "cssSelector", "commentSectionOfSendingEmail");
			if (commentSection.findElement(By.xpath("//span[contains(text(),'has shared this feed over Email with')]"))
					.isDisplayed()) {
				ExtentTestManager.getTest().log(Status.PASS, "Share over email functionality is working successfully");
			} else {
				ExtentTestManager.getTest().log(Status.FAIL,
						"Comment is not generated even after sharing sharing this feed over email");
			}
			findElement(driver, "xpath", "closeMailButton").click();
			functionVerify = true;
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "Error in try block " + e.toString());
		}
		return functionVerify;
	}

	public boolean incomingEmailAsPrivateMessageCheck() {
		boolean functionVerify = false;
		try {
			// ,"satheesh.tech5group@hubmail.mangoapps.com",
			String to[] = { prop.getProp("individualEmail") };
			sendEmail("mangorobot12@gmail.com", to, "this is to test email functionality", "email test");
			// sendEmail("satheeshg@mangospring.com", to, "this is to test email
			// functionality", "email test");
			ExtentTestManager.getTest().log(Status.PASS, "Email is sent from external mail system to application");
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ms-dash-inbox-dm-feeds-count-d")));
			getURL("mailTabURL");
			findElement(driver, "xpath", "privateMailSubject").click();
			findElement(driver, "xpath", "markMessageRead").click();
			Thread.sleep(2000);
			ExtentTestManager.getTest().log(Status.PASS, "Email is received as a private message");
			functionVerify = true;
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "Error in try block " + e.toString());
		}
		return functionVerify;
	}

	public boolean OutgoingEmailViaShareCheck() {
		boolean functionVerify = false;
		try {
			Thread.sleep(5000);
			readEmail("check Outgoing Email");
			ExtentTestManager.getTest().log(Status.PASS, "Outgoing email validation is successful");
			functionVerify = true;
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "Error in try block " + e.toString());
		}
		return functionVerify;
	}

	public boolean ViewCounterCheck() {
		boolean functionVerify = false;
		boolean functionVerify1 = false;
		boolean functionVerify2 = false;
		boolean functionVerify3 = false;
		try {

			ExtentTestManager.getTest().log(Status.INFO, "Copied URL of the Post is: " + postURL);
			ExtentTestManager.getTest().log(Status.INFO, "Copied URL of the Wiki is: " + wikiURL);
			ExtentTestManager.getTest().log(Status.INFO, "Copied URL of the Page is: " + pageURL);
			driver.get(postURL);
			Thread.sleep(2000);
			driver.navigate().refresh();
			Thread.sleep(2000);
			driver.navigate().refresh();
			Thread.sleep(1000);
			ExtentTestManager.getTest().log(Status.PASS, "Post View count should become 4 now");
			driver.get(wikiURL);
			driver.navigate().refresh();
			driver.navigate().refresh();
			ExtentTestManager.getTest().log(Status.PASS, "Wiki View count should become 4 now");
			driver.get(pageURL);
			driver.navigate().refresh();
			driver.navigate().refresh();
			ExtentTestManager.getTest().log(Status.PASS, "Page View count should become 4 now");
			logoutCheck();
			loginCheck(prop.getProp("secondUserEmail"), prop.getProp("GodPassword"));
			driver.get(postURL);
			ExtentTestManager.getTest().log(Status.PASS, "Post View count should become 5 now");
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(text(),'5 Views')]")));
			WebElement actualPostViewCount = findElement(driver, "xpath", "postViewCount");
			ExtentTestManager.getTest().log(Status.PASS,
					"Actual View count of Post is: " + actualPostViewCount.getText());
			if (actualPostViewCount.getText().equalsIgnoreCase("5 Views")) {
				ExtentTestManager.getTest().log(Status.PASS, "Post view count verified successfully");
				functionVerify1 = true;
				// actualPostViewCount.click();
				// Thread.sleep(2000);
				// List<WebElement>
				// individualViewCount=driver.findElements(By.cssSelector(".user_list_all.userDetailsbox_listView"));
				// individualViewCount.get(1).
			} else {
				ExtentTestManager.getTest().log(Status.FAIL, "Count mismatch: Please verify manually");
			}
			driver.get(wikiURL);
			ExtentTestManager.getTest().log(Status.PASS, "Wiki View count should become 5 now");
			String actualWikiViewCount = findElement(driver, "xpath", "wikiViewCount").getText();
			ExtentTestManager.getTest().log(Status.PASS, "Actual View count of Wiki is: " + actualWikiViewCount);
			if (actualWikiViewCount.equalsIgnoreCase("5 Views")) {
				ExtentTestManager.getTest().log(Status.PASS, "Wiki view count verified successfully");
				functionVerify2 = true;
			} else {
				ExtentTestManager.getTest().log(Status.FAIL, "Count mismatch: Please verify manually");
			}
			driver.get(pageURL);
			ExtentTestManager.getTest().log(Status.PASS, "Page View count should become 6 now");
			WebElement actualPageViewCount = findElement(driver, "xpath", "pageViewCount");
			scrollToView(driver, actualPageViewCount);
			Thread.sleep(2000);
			ExtentTestManager.getTest().log(Status.PASS,
					"Actual View count of Page is: " + actualPageViewCount.getText());
			if (actualPageViewCount.getText().equalsIgnoreCase("6 Views")) {
				ExtentTestManager.getTest().log(Status.PASS, "Page view count verified successfully");
				functionVerify3 = true;
			} else {
				ExtentTestManager.getTest().log(Status.FAIL, "Count mismatch: Please verify manually");
			}
			if (functionVerify1 && functionVerify2 && functionVerify3) {
				functionVerify = true;
			}
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "Error in try block " + e.toString());
		}
		return functionVerify;
	}

	public boolean fileDeepSearchCheck() {
		boolean functionVerify = false;
		try {
			driver.get(domainURL);
			ExtentTestManager.getTest().log(Status.PASS, "Entered to the Landing Page of the domain");
			explicitWait(driver, "id", "globalSearchBox");
			findElement(driver, "id", "globalSearchBox").click();
			Thread.sleep(1000);
			findElement(driver, "id", "globalSearchBox").sendKeys("this is to test file next version upload flow");
			ExtentTestManager.getTest().log(Status.PASS, "Entered the text String in Global search Box");
			Thread.sleep(1000);
			findElement(driver, "id", "globalSearchBox").sendKeys(Keys.ENTER);
			Thread.sleep(3000);
			WebElement desiredFile = findElement(driver, "xpath", "fileSearchResult");
			scrollToView(driver, desiredFile);
			if (desiredFile.getText().contains("sampleTest")) {
				ExtentTestManager.getTest().log(Status.PASS, "File Deep Search is Successful");
				functionVerify = true;
			} else {
				ExtentTestManager.getTest().log(Status.INFO, "Entered Text Doesn't Match to any File");
			}

		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.INFO, e.toString());
			ExtentTestManager.getTest().log(Status.FAIL, "Failed due to exception");
		}
		return functionVerify;
	}

	public boolean makeAFolderPublicCheck() {
		boolean functionVerify = false;
		try {
			getURL("projectTabURL");
			ExtentTestManager.getTest().log(Status.INFO, "Entered into Project Module");
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Automation Test")));
			findElement(driver, "linkText", "createProjectName").click();
			ExtentTestManager.getTest().log(Status.INFO, "Entered into Desired Project");
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Files")));
			findElement(driver, "linkText", "projectFilesTab").click();
			ExtentTestManager.getTest().log(Status.PASS, "Successfully clicked on Project Files Tab");
			explicitWait(driver, "xpath", "newProjectFileButton");
			findElement(driver, "xpath", "newProjectFileButton").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on New file Drop Down");
			Thread.sleep(2000);
			WebElement createNewFolder = findElement(driver, "cssSelector", "createNewSubFolder");
			mouseHover(driver, createNewFolder);
			createNewFolder.click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on make a new sub folder");
			WebElement folderName = findElement(driver, "id", "projectFolderName");
			wait.until(ExpectedConditions.elementToBeClickable(folderName));
			folderName.clear();
			folderName.sendKeys("Test Public Folder " + dateName);
			Thread.sleep(1000);
			findElement(driver, "xpath", "folderCreationDone").click();
			Thread.sleep(1000);
			findElement(driver, "xpath", "addedNewFolder").click();
			Thread.sleep(1000);
			findElement(driver, "xpath", "newProjectFileButton").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on New file Drop Down");
			Thread.sleep(1000);
			WebElement uploadNewFile = findElement(driver, "cssSelector", "newFileUpload");
			mouseHover(driver, uploadNewFile);
			uploadNewFile.click();
			ExtentTestManager.getTest().log(Status.PASS, "Moved on to Upload New File and clicked on it");
			WebElement notify = driver.findElement(By.xpath("//*[@id=\"notice\"]/a"));
			try {
				notify.click();
				ExtentTestManager.getTest().log(Status.INFO, "Closed the Notification bar");
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			findElement(driver, "id", "addFilesButton").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Add Files Button");
			Thread.sleep(1000);
			upload(driver, "textFilePath");
			wait.until(ExpectedConditions.elementToBeClickable(findElement(driver, "id", "filesAddDoneButton")));
			findElement(driver, "id", "filesAddDoneButton").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Done Button");
			Thread.sleep(2000);
			findElement(driver, "xpath", "leftSideProjectName").click();
			WebElement folderOnLeft = findElement(driver, "xpath", "leftSideFolderName");
			Actions myAct = new Actions(driver);
			Thread.sleep(2000);
			myAct.contextClick(folderOnLeft).build().perform();
			ExtentTestManager.getTest().log(Status.PASS, "Right Clicked on Folder");
			Thread.sleep(3000);
			WebElement shareFolder = findElement(driver, "class", "shareFolder");
			mouseHover(driver, shareFolder);
			shareFolder.click();
			ExtentTestManager.getTest().log(Status.PASS, "Selected Share Folder Option");
			Thread.sleep(2000);
			findElement(driver, "cssSelector", "shareFolderPublic").click();
			ExtentTestManager.getTest().log(Status.PASS, "Selected Share Folder Public Option");
			Thread.sleep(2000);
			findElement(driver, "xpath", "makePublicButton").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on make Folder Public Option");
			wait.until(ExpectedConditions.visibilityOf(findElement(driver, "id", "publicLink")));
			String folderLink = findElement(driver, "id", "publicLink").getAttribute("value");
			ExtentTestManager.getTest().log(Status.PASS, "Copied the folder link into a String");
			Thread.sleep(1000);
			findElement(driver, "xpath", "sharePermissionsCloseButton").click();
			ExtentTestManager.getTest().log(Status.PASS, "Closed the share permissions Window");
			Thread.sleep(1000);
			openNewTab(driver);
			ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
			driver.switchTo().window(tabs.get(1));
			driver.get(folderLink);
			if (findElement(driver, "xpath", "filefromFolder").isDisplayed()) {
				Thread.sleep(1000);
				ExtentTestManager.getTest().log(Status.PASS,
						"Folder has been made public and able to view its files from outside");
				functionVerify = true;
			}
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "Error in try block " + e.toString());
		}
		return functionVerify;
	}

	public boolean wikiTableOfContentsCheck() {
		boolean functionVerify = false;
		try {
			driver.get(wikiURL);
			WebElement tableOfContent = findElement(driver, "id", "wikiTableOfContents");
			if (tableOfContent.getAttribute("class").equalsIgnoreCase("table_of_contents")) {
				ExtentTestManager.getTest().log(Status.PASS,
						"Currently the table of Contents is closed because class value is: "
								+ tableOfContent.getAttribute("class"));
				tableOfContent.click();
				ExtentTestManager.getTest().log(Status.INFO, "Clicked on the Table of Contents");
				Thread.sleep(2000);
				if (tableOfContent.getAttribute("class").equalsIgnoreCase("table_of_contents shown")) {
					ExtentTestManager.getTest().log(Status.PASS,
							"Now the table of Contents is Open because class value is: "
									+ tableOfContent.getAttribute("class"));
				}
			} else if (tableOfContent.getAttribute("class").equalsIgnoreCase("table_of_contents shown")) {
				ExtentTestManager.getTest().log(Status.PASS,
						"Currently the table of Contents is Open because class value is: "
								+ tableOfContent.getAttribute("class"));
				tableOfContent.click();
				ExtentTestManager.getTest().log(Status.INFO, "Clicked on the Table of Contents");
				Thread.sleep(2000);
				if (tableOfContent.getAttribute("class").equalsIgnoreCase("table_of_contents")) {
					ExtentTestManager.getTest().log(Status.PASS,
							"Now the table of Contents is Open because class value is: "
									+ tableOfContent.getAttribute("class"));
				}
			}
			ExtentTestManager.getTest().log(Status.PASS, "Passed sampleTest Successfully");
			functionVerify = true;
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.INFO, e.toString());
		}
		return functionVerify;
	}

	public boolean sampleTest() {
		boolean functionVerify = false;
		try {

			ExtentTestManager.getTest().log(Status.PASS, "Passed sampleTest Successfully");
			functionVerify = true;
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "Error in try block " + e.toString());
		}
		return functionVerify;
	}

	public boolean sampleTest2() {
		boolean functionVerify = false;
		try {
			ExtentTestManager.getTest().log(Status.INFO, "This is to test sampleTest");
			ExtentTestManager.getTest().log(Status.PASS, "Passed sampleTest Successfully");
			functionVerify = true;
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "Error in try block " + e.toString());
		}
		return functionVerify;
	}

//================================================================= Supporting methods ====================================================================================
	public String getScreenshot(String screenshotName) throws Exception {
		TakesScreenshot ts = (TakesScreenshot) driver;
		File source = ts.getScreenshotAs(OutputType.FILE);
		// after execution, you could see a folder "FailedTestsScreenshots" under src
		// folder
		String destination = dir + "\\FailedTestsScreenshots\\" + screenshotName + dateName + ".png";
		File finalDestination = new File(destination);
		FileUtils.copyFile(source, finalDestination);
		return destination;
	}

	public void uploadFuntionality(String documentPathName) {
		try {
			getURL("projectTabURL");
			ExtentTestManager.getTest().log(Status.INFO, "Entered into Project Module");
			findElement(driver, "linkText", "createProjectName").click();
			ExtentTestManager.getTest().log(Status.INFO, "Entered into Desired Project");
			explicitWait(driver, "linkText", "projectFilesTab");
			findElement(driver, "linkText", "projectFilesTab").click();
			ExtentTestManager.getTest().log(Status.PASS, "Successfully clicked on Project Files Tab");
			explicitWait(driver, "xpath", "newProjectFileButton");
			findElement(driver, "xpath", "newProjectFileButton").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on New file Drop Down");
			Thread.sleep(1000);
			WebElement uploadNewFile = findElement(driver, "cssSelector", "newFileUpload");
			mouseHover(driver, uploadNewFile);
			uploadNewFile.click();
			ExtentTestManager.getTest().log(Status.PASS, "Moved on to Upload New File and clicked on it");
			Thread.sleep(1000);
			findElement(driver, "id", "addFilesButton").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Add Files Button");
			Thread.sleep(1000);
			upload(driver, documentPathName);
			/* this is the code to handle mandatory field(location) at upload time
			 * try {
				WebElement location=findElement(driver, "xpath", "uploadLocation");
				wait.until(ExpectedConditions.elementToBeClickable(location));
				Select locSelect=new Select(location);
				locSelect.selectByVisibleText("USA");
			}catch (Exception e) {
				ExtentTestManager.getTest().log(Status.INFO, "Location element is not Present " + e.toString());
				System.out.println("Error in try block " + e.toString());
			}  */
			wait.until(ExpectedConditions.elementToBeClickable(findElement(driver, "id", "filesAddDoneButton")));
			findElement(driver, "id", "filesAddDoneButton").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Done Button");
			Thread.sleep(3000);
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "Error in try block " + e.toString());
		}
	}

	public boolean loginCheck(String userName, String password) {
		boolean functionVerify = false;
		try {
			ExtentTestManager.getTest().log(Status.PASS, "Running on domain: " + domainURL);
			findElement(driver, "id", "userID").clear();
			findElement(driver, "id", "userID").sendKeys(userName);
			ExtentTestManager.getTest().log(Status.PASS, "Entered Username Successfully");
			findElement(driver, "id", "password").clear();
			findElement(driver, "id", "password").sendKeys(password);
			ExtentTestManager.getTest().log(Status.PASS, "Entered password Successfully");
			findElement(driver, "id", "loginButton").click();
			ExtentTestManager.getTest().log(Status.PASS, "Clicked on Login Button Successfully");
			explicitWait(driver, "id", "globalSearchBox");
			WebElement notify = driver.findElement(By.xpath("//*[@id=\"notice\"]/a"));
			try {
				notify.click();
				ExtentTestManager.getTest().log(Status.INFO, "Closed the Notification bar");
			} catch (Exception e) {
				ExtentTestManager.getTest().log(Status.INFO, "Pop-out is not shown up/not handled properly");
			}
			functionVerify = true;
			ExtentTestManager.getTest().log(Status.PASS, "Logged In Successfully");
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "Error in try block " + e.toString());
			driver.navigate().refresh();
			loginCheck(userName, password);
		}
		return functionVerify;
	}
}
