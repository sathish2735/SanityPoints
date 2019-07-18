package mangoMain.listener;

import java.io.IOException;

import javax.mail.MessagingException;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.Status;

import utilities.ExtentManager;
import utilities.ExtentTestManager;
import utilities.FunctionLibrary;
import utilities.GetProperty;
import utilities.ReusableAssets;

public class TestListener implements ITestListener {
	FunctionLibrary FLib = new FunctionLibrary();
	public static GetProperty prop = new GetProperty();
	public int totalpass=0;
	public int totalfail=0;
	public int totalskip=0;
	public void onStart(ITestContext context) {
		System.out.println("*** Test Suite " + context.getName() + " started ***");
	}

	public void onFinish(ITestContext context) {
		System.out.println(("*** Test Suite " + context.getName() + " ending ***"));
		ExtentTestManager.endTest();
		ExtentManager.getInstance().flush();
		System.out.println("Total pass count is: "+totalpass);
		System.out.println("Total fail count is: "+totalfail);
		System.out.println("Total skipped count is: "+totalskip);
		try {
			final String to[]= {prop.getProp("teamEmailAddress")};
			ReusableAssets.sendFinaReportEmail("mangorobot12@gmail.com", to, totalpass, totalfail, totalskip);
		} catch (MessagingException e) {
			System.out.println(e.toString());
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}

	public void onTestStart(ITestResult result) {
		System.out.println(("*** Running test method " + result.getMethod().getMethodName() + "..."));
		ExtentTestManager.startTest(result.getMethod().getMethodName());
	}

	public void onTestSuccess(ITestResult result) {
		System.out.println("*** Executed " + result.getMethod().getMethodName() + " test successfully...");
		ExtentTestManager.getTest().log(Status.PASS, "Test passed from listener");
		totalpass=totalpass+1;
	}

	public void onTestFailure(ITestResult result) {
		System.out.println("*** Test execution " + result.getMethod().getMethodName() + " failed...");
		try {
			ExtentTestManager.getTest().log(Status.INFO, "SnapshotBelow: " + ExtentTestManager.getTest()
					.addScreenCaptureFromPath(FLib.getScreenshot(result.getMethod().getMethodName())));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ExtentTestManager.getTest().log(Status.INFO, "Executing in catch " + e.getMessage());
		}
		ExtentTestManager.getTest().log(Status.FAIL, "Test Failed from Listener");
		totalfail=totalfail+1;
	}

	public void onTestSkipped(ITestResult result) {
		System.out.println(("*** Running test method " + result.getMethod().getMethodName() + "..."));
		ExtentTestManager.startTest(result.getMethod().getMethodName());
		System.out.println("*** Test " + result.getMethod().getMethodName() + " skipped...");
		ExtentTestManager.getTest().log(Status.SKIP, "Test Skipped");
		totalskip=totalskip+1;
	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		System.out.println("*** Test failed but within percentage % " + result.getMethod().getMethodName());
	}

}
