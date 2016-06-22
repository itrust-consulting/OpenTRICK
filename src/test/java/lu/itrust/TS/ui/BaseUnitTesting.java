package lu.itrust.TS.ui;

import static org.testng.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import com.google.common.io.Files;

import lu.itrust.TS.ui.driver.DriverBuilder;
import lu.itrust.TS.ui.driver.DriverType;

public class BaseUnitTesting {

	private boolean acceptNextAlert = true;

	private StringBuffer verificationErrors = new StringBuffer();

	private Pattern inputPattern = Pattern.compile("input|select|textarea");

	protected Pattern getInputPattern() {
		return inputPattern;
	}

	private String baseUrl;

	private DriverType driverType;

	private String path;

	private boolean debug;

	@BeforeClass(alwaysRun = true)
	@Parameters(value = { "baseurl", "driver.type", "driver.path", "debug" })
	public void setUp(String url, DriverType driverType, String path, boolean debug) throws Exception {
		this.baseUrl = url;
		this.driverType = driverType;
		this.path = path;
		this.debug = debug;
	}

	@BeforeTest(alwaysRun = true)
	@AfterSuite(alwaysRun = true)
	public void closeDriver() throws InterruptedException {
		if (driverType != null)
			DriverBuilder.getInstanceDriver(driverType).close();
	}

	@AfterMethod(alwaysRun = true)
	public void screenshot(ITestResult testResult) throws IOException, InterruptedException {
		if ((testResult.getStatus() == ITestResult.FAILURE) || (testResult.getStatus() == ITestResult.SKIP)) {

			if (debug) {
				File scrFile = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
				String failureImageFileName = new SimpleDateFormat("MM-dd-yyyy_HH-ss")
						.format(new GregorianCalendar().getTime()) + ".png";
				String destDir = System.getProperty("user.dir") + "/" + "test-output/screenshots/"
						+ testResult.getMethod().getMethodName();
				new File(destDir).mkdirs();
				Files.copy(scrFile, new File(destDir + "/" + failureImageFileName));
			}
			
			closeDriver();
		}
	}

	@AfterClass(alwaysRun = true)
	public void tearDown() throws Exception {

		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}

	protected boolean isElementPresent(By by) {

		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), 1);
			wait.until(ExpectedConditions.presenceOfElementLocated(by));
			return findElement(by) != null;
		} catch (Exception e) {
			return false;
		}
	}

	protected boolean isAlertPresent() {
		try {
			getDriver().switchTo().alert();
			return true;
		} catch (NoAlertPresentException e) {
			return false;
		}
	}

	protected String closeAlertAndGetItsText() {
		try {
			Alert alert = getDriver().switchTo().alert();
			String alertText = alert.getText();
			if (acceptNextAlert) {
				alert.accept();
			} else {
				alert.dismiss();
			}
			return alertText;
		} finally {
			acceptNextAlert = true;
		}
	}

	// private functions
	protected WebElement findElement(By by) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), 1);
			wait.until(ExpectedConditions.visibilityOfElementLocated(by));
			return getDriver().findElement(by);
		} catch (Exception e) {
			return null;
		}
	}

	protected WebElement findElementByCss(String query) {
		try {
			return findElement(By.cssSelector(query));
		} catch (Exception e) {
			return null;
		}
	}

	protected WebElement findSubElement(WebElement element, By by) {
		try {
			return element.findElement(by);
		} catch (Exception e) {
			return null;
		}
	}

	protected void login(String username, String password) {
		getDriver().get(baseUrl + "/Login");
		findElement(By.id("username")).sendKeys(username);
		findElement(By.name("password")).sendKeys(password);
		findElement(By.id("login_signin_button")).click();
	}

	protected WebDriver getDriver() {
		return DriverBuilder.getInstanceDriver(driverType).getDriver(path);
	}

	protected void sendKeys(WebElement element, String msg) {

		try {
			int count = 0;
			WebDriverWait wait = new WebDriverWait(getDriver(), 1);
			wait.until(ExpectedConditions.visibilityOf(element));
			while (count < 10 && !getTextFromTextareaInput(element).equals(msg)) {

				continueClearingUntilClear(element);
				element.click();
				element.getText();

				element.sendKeys(msg);
				Thread.sleep(1);
				count++;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String getTextFromTextareaInput(WebElement element) {

		if (element.getAttribute("value") == null) {
			return element.getText();
		} else {
			return element.getAttribute("value");
		}
	}

	private void continueClearingUntilClear(WebElement element) throws InterruptedException {
		int count = 0;
		while (count < 10 && !element.getAttribute("value").isEmpty()) {
			element.clear();
			Thread.sleep(1);
			count++;
		}
	}

	protected void signOut() throws InterruptedException {
		click(By.xpath("//a[contains(@onclick,'#logoutFormSubmiter')]"));
		getDriver().navigate().refresh();
	}

	protected boolean canBeClicked(WebElement webe) {
		try {
			new Actions(getDriver()).clickAndHold(webe);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	protected boolean isVisible(WebElement webe) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), 1);
			wait.until(ExpectedConditions.visibilityOf(webe));
			return true;
		} catch (TimeoutException e) {
			return false;
		}
	}

	protected boolean waitClick(By by) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), 1);
			wait.until(ExpectedConditions.elementToBeClickable(findElement(by)));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	protected void scrollToElement(WebElement el) {
		if (getDriver() instanceof JavascriptExecutor) {
			((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(true);", el);
		}
	}

	protected void chooseElementInsideDropdown(String dropdownMenuItemXpath) throws InterruptedException {
		click(By.xpath(dropdownMenuItemXpath + "//ancestor::*[contains(@class,'dropdown-submenu')]"));
		click(By.xpath(dropdownMenuItemXpath));
	}

	//
	protected void goToKnowledgeBase() throws InterruptedException {
		click(By.xpath("//a[substring-before(@href,'/KnowledgeBase')]"));
	}

	protected void waitLoadingIndicator() {
		WebDriverWait wait = new WebDriverWait(getDriver(), 30);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("#loading-indicator")));
		assert !isElementPresent(By.id("#loading-indicator"));
	}

	protected void click(By by) throws InterruptedException {
		waitClick(by);
		canBeClicked(findElement(by));
		try {
			findElement(by).click();
		} catch (Exception e) {
			waitClick(by);
			canBeClicked(findElement(by));
			findElement(by).click();
		}
	}

	protected void selectCheckBox(boolean stateToBecome, By by) throws InterruptedException {
		if (findElement(by).isSelected() != stateToBecome) {
			click(by);
		}
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public boolean isDebug() {
		return debug;
	}
	
	

}
