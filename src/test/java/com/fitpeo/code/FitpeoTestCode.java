package com.fitpeo.code;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.FileReader;
import java.time.Duration;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fitpeo.testcases.FitpeoAssessment;

public class FitpeoTestCode {

	static WebDriver driver = null;
	WebDriverWait wait = null;
	JavascriptExecutor js = null;

	public FitpeoTestCode(WebDriver driver) {
		FitpeoTestCode.driver = driver;
		PageFactory.initElements(driver, this);
		wait = new WebDriverWait(FitpeoTestCode.driver, Duration.ofSeconds(30));
		js = (JavascriptExecutor) FitpeoTestCode.driver;
	}

	@FindBy(xpath = "//a[contains(@href,'revenue-calculator')]")
	private WebElement revenueCalculatorLink;

	@FindBy(xpath = "//h4[text()='Medicare Eligible Patients']")
	private WebElement scrollToSliderSection;

	@FindBy(xpath = "//span[contains(@class,'thumbSizeMedium')]/input")
	private WebElement slider;

	@FindBy(xpath = "//input[@type='number']")
	private WebElement sliderInputText;

	@FindBy(xpath = "//p[text()='Total Recurring Reimbursement for all Patients Per Month:']//child::p")
	private WebElement totalReimbursement;

	public boolean launchApplication() {
		boolean flag = false;
		try {

			// Launching the application
			driver.get(FitpeoTestCode.getTheSpecifiedProperty("ApplicationURL"));

			// validating the successful launch of application
			String name = driver.getTitle();
			if ("Remote Patient Monitoring (RPM) - fitpeo.com".equals(name)) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	public boolean navigateToRevenueCalculator() {
		boolean flag = false;
		try {
			// Navigating to Revenue Calculator
			revenueCalculatorLink.click();
			wait.until(ExpectedConditions.visibilityOfAllElements(scrollToSliderSection));
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	public boolean navigateToSLiderSection() {
		boolean flag = false;
		try {
			// Navigating to Slider Section
			js.executeScript("arguments[0].scrollIntoView();", scrollToSliderSection);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	public boolean adujustingSliderToRespectiveValue() {
		boolean flag = false;
		try {

			// Adjusting the slider
			Actions action = new Actions(driver);
			action.dragAndDropBy(slider, 4, 0).build().perform();// dragAndDropBy(slider, 0, 0).build().perform();
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	public boolean adujustingSliderTextToGivenValue() {
		boolean flag = false;
		try {
			String expected = FitpeoTestCode.getTheSpecifiedProperty("ValueToEnterInRevenueCalculator2");
			String textValue = sliderInputText.getAttribute("value");
			sliderInputText.click();
			Robot robot = new Robot();
			for (int i = 0; i < textValue.length(); i++) {
				robot.keyPress(KeyEvent.VK_BACK_SPACE);
				robot.keyRelease(KeyEvent.VK_BACK_SPACE);
			}
			sliderInputText.sendKeys(expected);
			String actual = slider.getDomAttribute("value");
			if (expected.equals(actual)) {
				flag = true;
			}
			String expected2 = FitpeoTestCode.getTheSpecifiedProperty("ValueToEnterInRevenueCalculator1");
			sliderInputText.click();
			for (int i = 0; i < textValue.length(); i++) {
				robot.keyPress(KeyEvent.VK_BACK_SPACE);
				robot.keyRelease(KeyEvent.VK_BACK_SPACE);
			}
			sliderInputText.sendKeys(expected2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	public boolean checkBoxesOfCPTCodes() {
		boolean flag1 = false;
		boolean flag2 = false;
		boolean flag = false;
		try {
			String[] cptvalues = FitpeoTestCode.getTheSpecifiedProperty("CPTValues").split(",");
			int count = 0;
			int sum = 0;
			for (int i = 0; i < cptvalues.length; i++) {
				driver.findElement(By.xpath("//p[text()='" + cptvalues[i] + "']//following-sibling::label/span/input"))
						.click();
				FitpeoAssessment.resultStatus("INFO", "Selected CPT Code " + cptvalues[i]);
				WebElement ele = driver.findElement(By.xpath("//p[text()='" + cptvalues[i]
						+ "']//following::div[contains(@class,'outlinedSuccess')]//span"));
				if (ele.getText().contains("Recurring in 30 days")) {
					String value = driver
							.findElement(
									By.xpath("//p[text()='" + cptvalues[i] + "']//following-sibling::label//span[2]"))
							.getText();
					sum = sum + Integer.parseInt(value);
				}
				count = count + 1;
			}
			if (count == cptvalues.length) {
				flag1 = true;
			}
			long expValueReim = sum
					* (Integer.parseInt(FitpeoTestCode.getTheSpecifiedProperty("ValueToEnterInRevenueCalculator1")));
			long actvalueReim = Long.parseLong(totalReimbursement.getText().replace("$", ""));
			if (expValueReim == actvalueReim) {
				flag2 = true;
			}

			flag = flag1 && flag2;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	public static String getTheSpecifiedProperty(String key) {
		String path = System.getProperty("user.dir") + "\\src\\test\\resources\\Configuration\\Web.properties";
		String value = null;
		try {
			FileReader reader = new FileReader(path);
			Properties props = new Properties();
			props.load(reader);
			value = props.getProperty(key);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

}
