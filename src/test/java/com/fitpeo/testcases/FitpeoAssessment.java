package com.fitpeo.testcases;

import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.fitpeo.code.FitpeoTestCode;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.testng.annotations.AfterClass;

public class FitpeoAssessment {

	public static WebDriver driver = null;
	public static ExtentSparkReporter sprkRep;
	public static ExtentReports extRep;
	public static ExtentTest testRep;
	FitpeoTestCode fitpeo = null;

	@BeforeClass
	@Parameters({ "browserName" })
	public void beforeClass(@Optional("CHROME") String browserName) {

		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		String repName = "Test-Report" + timeStamp + ".html";
		sprkRep = new ExtentSparkReporter("./reports/" + repName);
		sprkRep.config().setDocumentTitle("FitPeoAutomationAssesment");
		sprkRep.config().setTheme(Theme.DARK);
		extRep = new ExtentReports();
		extRep.attachReporter(sprkRep);

		// Initializing the driver
		switch (browserName) {
		case "CHROME":
			WebDriverManager.chromedriver().setup();
			driver = new ChromeDriver();
			driver.manage().window().maximize();
			break;

		case "EDGE":
			WebDriverManager.edgedriver().setup();
			driver = new EdgeDriver();
			driver.manage().window().maximize();
			break;
		case "IE":
			WebDriverManager.iedriver().setup();
			driver = new InternetExplorerDriver();
			driver.manage().window().maximize();
			break;
		case "FIREFOX":
			WebDriverManager.firefoxdriver().setup();
			driver = new FirefoxDriver();
			driver.manage().window().maximize();
			break;
		case "SAFARI":
			WebDriverManager.safaridriver().setup();
			driver = new SafariDriver();
			driver.manage().window().maximize();
			break;
		}

		fitpeo = new FitpeoTestCode(driver);

	}

	@Test
	public void testCases() {
		testRep = extRep.createTest("Assignment Test Cases");
		testRep.createNode("Assignment Test Cases");

		// Validating if application launched successfully
		if (fitpeo.launchApplication()) {
			FitpeoAssessment.resultStatus("PASS", "Application launched successfully");
		} else {
			FitpeoAssessment.resultStatus("FAIL", "Application not launched successfully");
		}

		// Validating if Revenue Calculator page loaded
		if (fitpeo.navigateToRevenueCalculator()) {
			FitpeoAssessment.resultStatus("PASS", "Naviagted to Revenue Calculator");
		} else {
			FitpeoAssessment.resultStatus("FAIL", "Unable to navigate Revenue Calculator");
		}

		// Scroll Down to the Slider section
		if (fitpeo.navigateToSLiderSection()) {
			FitpeoAssessment.resultStatus("PASS", "Scroll Down to the Slider section successfully");
		} else {
			FitpeoAssessment.resultStatus("FAIL", "Unable to Scroll Down to the Slider section");
		}

		// Adjust the Slider
		if (fitpeo.adujustingSliderToRespectiveValue()) {
			FitpeoAssessment.resultStatus("PASS", "Adjust the Slider");
		} else {
			FitpeoAssessment.resultStatus("FAIL", "Unable to Adjust the Slider");
		}

		// Update the Text Field and Validate Slider Value
		if (fitpeo.adujustingSliderTextToGivenValue()) {
			FitpeoAssessment.resultStatus("PASS", "Updated the Text Field and Validated Slider Value successfully");
		} else {
			FitpeoAssessment.resultStatus("FAIL", "Unable to update the Text Field in revenue calculator");
		}

		// Select CPT Codes
		if (fitpeo.checkBoxesOfCPTCodes()) {
			FitpeoAssessment.resultStatus("PASS",
					"Selected CPT Codes and Validated Total Recurring Reimbursement successfully");
		} else {
			FitpeoAssessment.resultStatus("FAIL",
					"Unable to select CPT select CPT Codes and validate Total Recurring Reimbursement");
		}

	}

	@AfterClass
	public void afterClass() {
		// closing the browser
		driver.quit();

		// Report
		extRep.flush();
	}

	public static void resultStatus(String status, String message) {
		try {
			String image = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
			if ("PASS".equals(status)) {
				FitpeoAssessment.testRep.log(Status.PASS, message,
						MediaEntityBuilder.createScreenCaptureFromBase64String(image).build());
			} else if ("FAIL".equals(status)) {
				FitpeoAssessment.testRep.log(Status.FAIL, message,
						MediaEntityBuilder.createScreenCaptureFromBase64String(image).build());
			} else if ("INFO".equals(status)) {
				FitpeoAssessment.testRep.log(Status.INFO, message,
						MediaEntityBuilder.createScreenCaptureFromBase64String(image).build());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
