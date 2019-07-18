package mangoMain.SanitySuite;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;

import utilities.ExtentTestManager;
import utilities.FunctionLibrary;
import utilities.ReusableAssets;
@Listeners(mangoMain.listener.TestListener.class)

public class TestRun extends FunctionLibrary {
	ReusableAssets reuse=new ReusableAssets();
	@BeforeTest
	public void browserStart() throws IOException
	{
		//extentReportInitiate();
		driverSetup(prop.getProp("browser"));
	}
	
	@Test(priority=0, dependsOnMethods="", enabled=false)
	public void domainSignUp() throws IOException	{
		Assert.assertEquals(domainSignUpCheck("signupURL"), true);
	}
	@Test(priority=1, enabled=false)
	public void userSingUp()	{
		Assert.assertEquals(domainLoginCheck(), true);		
	}
	@Test(priority=1)
	public void userExplicitLogin()	{
		Assert.assertEquals(loginCheck(), true);
		dataCenter();
	}
	@Test(priority=2)
	public void defaultGroups()	{
		Assert.assertEquals(defaultGroupsCheck(), true);
	}
	@Test(priority=3)
	public void allUsersAddedToEveryOneGroup()	{
		Assert.assertEquals(EveryoneAddedCheck(), true);
		//Need to execute this at the end of suite
		//Assert.assertEquals(verifyUsersInEveryoneCheck(), true);
	}

	@Test(priority=4)
	public void projectCreation()	{
		Assert.assertEquals(projectCreationCheck(), true);
	}
	@Test(priority=5)
	public void documentConversion()	{
		Assert.assertEquals(documentConversionCheck(), true);
	}
	@Test(priority=6)
	public void fileUploadNewVersion()	{
		Assert.assertEquals(fileUploadNewVersionCheck(), true);
	}
	
	@Test(priority=7)
	public void timeZoneSetup()	{
		Assert.assertEquals(timeZoneSetupCheck(), true);
	}
	@Test(priority=6)
	public void changeDP()	{
		Assert.assertEquals(changeDPCheck(), true);
	}
	@Test(priority=7)
	public void defaultLandingPageSetting()	{
		Assert.assertEquals(defaultLandingPageSettingCheck(), true);
	}

	@Test(priority=14)
	public void createPostFromCompose()	{
		Assert.assertEquals(createPostFromComposeCheck(), true);
	}
	
	@Test(priority=15)
	public void askQuestion()	{
		Assert.assertEquals(askQuestionCheck(), true);
	}
	
	@Test(priority=15)
	public void createWikiFromCompose()	{
		Assert.assertEquals(createWikiFromComposeCheck(), true);
	}
	
	@Test(priority=16)
	public void createStaticPage()	{
		Assert.assertEquals(createStaticPageCheck(), true);
	}
	
	@Test(priority=17)
	public void createDynamicPage()	{
		Assert.assertEquals(createDynamicPageCheck(), true);
	}
	
	@Test(priority=18)
	public void createRecurrenceTask()	{
		Assert.assertEquals(createReccurenceTaskCheck(), true);
	}
	
	@Test(priority=19)
	public void videoUploadNConversion()	{
		Assert.assertEquals(videoUploadNConversionCheck(), true);
	}
	
		
	@Test(priority=21)
	public void incomingEmailwithTeamID()	{
		Assert.assertEquals(incomingEmailwithTeamIDCheck(), true);
	}
	@Test(priority=22)
	public void incomingEmailReplyViaShare()	{
		Assert.assertEquals(incomingEmailReplyViaShareCheck(), true);
	}
	
	@Test(priority=22)
	public void OutgoingEmailViaShare()	{
		Assert.assertEquals(OutgoingEmailViaShareCheck(), true);
	}
	
	@Test(priority=23)
	public void incomingEmailAsPrivateMessage()	{
		Assert.assertEquals(incomingEmailAsPrivateMessageCheck(), true);
	}
	
	@Test(priority=25)
	public void verifyUsersInEveryone()	{
		Assert.assertEquals(verifyUsersInEveryoneCheck(), true);
	}
		
	@Test(priority=24)
	public void makeAFolderPublic()	{
		Assert.assertEquals(makeAFolderPublicCheck(), true);
	}
	@Test(priority=25)
	public void fileDeepSearch()	{
		Assert.assertEquals(fileDeepSearchCheck(), true);
	}
	@Test(priority=26)
	public void ViewCounter()	{
		Assert.assertEquals(ViewCounterCheck(), true);
	}
	
	@Test(priority=27)
	public void wikiTableOfContents()	{
		Assert.assertEquals(wikiTableOfContentsCheck(), true);
		driver.quit();
		ExtentTestManager.getTest().log(Status.PASS, "Closed the Chrome Driver");
		driverSetup("firefox");
		ExtentTestManager.getTest().log(Status.PASS, "Launched the Firefox Driver");
		Assert.assertEquals(loginCheck(), true);
		Assert.assertEquals(wikiTableOfContentsCheck(), true);
		driver.quit();
		ExtentTestManager.getTest().log(Status.PASS, "Closed the Firefox Driver");
		driverSetup("IE");
		ExtentTestManager.getTest().log(Status.PASS, "Launched the IE Driver");
		Assert.assertEquals(loginCheck(), true);
		Assert.assertEquals(wikiTableOfContentsCheck(), true);
		Assert.assertEquals(logoutCheck(), true);
		driver.quit();
		ExtentTestManager.getTest().log(Status.PASS, "Closed the IE Driver");
	}
	
	@Test(priority=28, enabled=false)
	public void logOutFromApplication()	{
		Assert.assertEquals(logoutCheck(), true);
	}
	
	@AfterTest
	public void writeReport(){
		// writing everything to document
		//flush() - to write or update test information to your report. 
           //     extent.flush();
	}
	@AfterSuite
	public void closeReport() {
		
		//Call close() at the very end of your session to clear all resources. 
        //If any of your test ended abruptly causing any side-affects (not all logs sent to ExtentReports, information missing), this method will ensure that the test is still appended to the report with a warning message.
        //You should call close() only once, at the very end (in @AfterSuite for example) as it closes the underlying stream. 
        //Once this method is called, calling any Extent method will throw an error.
        //close() - To close all the operation
       // extent.close();
	}
                
                

}
