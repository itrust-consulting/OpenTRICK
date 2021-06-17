package lu.itrust.TS.ui.tools;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import com.google.common.base.Function;
import com.google.common.io.Files;

import lu.itrust.TS.ui.driver.DriverBuilder;
import lu.itrust.TS.ui.driver.DriverType;
import lu.itrust.TS.ui.user.Login;

public class BaseUnitTesting {

	private static String baseUrl;

	private static boolean debug;

	private static String path;

	protected static DriverType driverType;

	protected static WebDriver getDriver() {
		return DriverBuilder.getInstanceDriver(driverType).getDriver(path);
	}

	private Pattern inputPattern = Pattern.compile("input|select|textarea");

	public String getBaseUrl() {
		return baseUrl;
	}

	@BeforeSuite(alwaysRun = true)
	@Parameters(value = { "baseurl", "driver.type", "driver.path", "debug" })
	public void init(String url, DriverType driverType, String path, boolean debug) throws Exception {
		BaseUnitTesting.baseUrl = url;
		BaseUnitTesting.driverType = driverType;
		BaseUnitTesting.path = path;
		BaseUnitTesting.debug = debug;
	}

	public boolean isDebug() {
		return debug;
	}

	/**
	 * @param e
	 */
	public void printError(Exception e) {
		if (debug) {
			e.printStackTrace();
		}
	}

	@AfterMethod(alwaysRun = true)
	public void screenshot(ITestResult testResult, ITestContext ctx) throws IOException, InterruptedException {
		if ((testResult.getStatus() == ITestResult.FAILURE) || (testResult.getStatus() == ITestResult.SKIP)) {
			if (debug) {

				File scrFile = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
				String failureImageFileName = new SimpleDateFormat("MM-dd-yyyy_HH-ss")
						.format(new GregorianCalendar().getTime()) + ".png";
				String destDir = ctx.getOutputDirectory() + "/screenshots/" + testResult.getMethod().getMethodName();
				System.out.println(ctx.getOutputDirectory() + "/screenshots/" + testResult.getMethod().getMethodName());
				new File(destDir).mkdirs();
				Files.copy(scrFile, new File(destDir + "/" + failureImageFileName));
			}

			if (ctx.getName().equals("firstInstallation")) {
				DriverBuilder.getInstanceDriver(driverType).setSkipTests(true);
			}
		}
		System.out.println(String.format("Class : %s, test: %s, status: %s",
				testResult.getMethod().getRealClass().getName(), testResult.getMethod().getMethodName(),
				testResult.getStatus() == ITestResult.SUCCESS ? "SUCCESS"
						: testResult.getStatus() == ITestResult.FAILURE ? "FAILURE"
								: testResult.getStatus() == ITestResult.SKIP ? "SKIP"
										: testResult.getStatus() == ITestResult.STARTED ? "STARTED"
												: "SUCCESS_PERCENTAGE_FAILURE"));
	}

	/**
	 * @param by
	 * @param index
	 * @throws InterruptedException
	 */
	public void selectComboboxByIndex(By by, int index) throws InterruptedException {
		int tries = 0;
		int cIndex = Integer.parseInt(new Select(findElement(by)).getFirstSelectedOption().getAttribute("index"));
		NeverStaleWebElement element = new NeverStaleWebElement(getDriver(), by);
		while (tries < 9999 && cIndex != index) {
			if (cIndex < index) {
				for (int i = 0; i < index - cIndex; i++) {
					element.sendKeys(Keys.ARROW_DOWN);
				}
			} else {
				for (int i = 0; i < cIndex - index; i++) {
					element.sendKeys(Keys.ARROW_UP);
				}
			}
			cIndex = Integer.parseInt(new Select(findElement(by)).getFirstSelectedOption().getAttribute("index"));
			tries++;
		}
	}

	@BeforeTest(alwaysRun = true)
	private void checkShouldSkip() {
		assert !DriverBuilder.getInstanceDriver(driverType).isSkipTests();
	}

	@AfterSuite(alwaysRun = true)
	private void closeDriver() throws InterruptedException {
		if (driverType != null)
			DriverBuilder.getInstanceDriver(driverType).close();
	}

	private void continueClearingUntilClear(WebElement element) throws InterruptedException {
		int count = 0;
		while (count < 10 && !element.getAttribute("value").isEmpty()) {
			element.clear();
			Thread.sleep(1);
			count++;
		}
	}

	private String getTextFromTextareaInput(WebElement element) {

		if (element.getAttribute("value") == null) {
			return element.getText();
		} else {
			return element.getAttribute("value");
		}
	}

	protected boolean canBeClicked(WebElement webe) {
		try {
			assert webe != null;
			new Actions(getDriver()).clickAndHold(webe).release();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/*
	 * protected WebElement findElementByCss(String query) { try { return
	 * findElement(By.cssSelector(query)); } catch (Exception e) { return null;
	 * } }
	 */

	protected void chooseElementInsideDropdown(String dropdownMenuItemXpath, boolean skipError)
			throws InterruptedException {
		try {
			click(By.xpath(dropdownMenuItemXpath + "//ancestor::*[contains(@class,'dropdown-submenu')]"));
			click(By.xpath(dropdownMenuItemXpath));
		} catch (TimeoutException e) {
			if (!skipError)
				throw e;
		}
	}

	protected void click(By by) throws InterruptedException {

		if (!isElementPresent(by)) {
			System.err.println("Error");
			assert !waitClick(by);
		}
		new WebDriverWait(getDriver(), 40).until(
				ExpectedConditions.and(ExpectedConditions.invisibilityOfElementLocated(By.id("#loading-indicator")),
						ExpectedConditions.elementToBeClickable(by)));

		new WebDriverWait(getDriver(), 10).until(new Function<WebDriver, Boolean>() {
			public Boolean apply(WebDriver driver) throws TimeoutException {
				try {
					driver.findElement(by).click();
					return true;
				} catch (WebDriverException webDriverException) {
					return false;
				}
			}
		});
	}

	// private functions
	protected WebElement findElement(By by) {
		try {
			new WebDriverWait(getDriver(), 10).until(ExpectedConditions.visibilityOfElementLocated(by));
			return getDriver().findElement(by);
		} catch (Exception e1) {
			return null;
		}
	}

	protected List<WebElement> findElements(By by) {
		try {
			new WebDriverWait(getDriver(), 10).until(ExpectedConditions.visibilityOfElementLocated(by));
			return getDriver().findElements(by);
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	protected WebElement findSubElement(WebElement element, By by) {
		try {
			return element.findElement(by);
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	protected Pattern getInputPattern() {
		return inputPattern;
	}

	protected void goToAllAnalysis(String companyName, String analyseName) throws InterruptedException {
		getDriver().get(baseUrl + "/Analysis/All");
		System.out.println("Company : " + companyName);
		new Select(findElement(By.id("customerSelectorFilter"))).selectByVisibleText(companyName);
		if (analyseName != null)
			selectAnalysis(analyseName);
		new WebDriverWait(getDriver(), 2).until(ExpectedConditions.presenceOfElementLocated(By.id("section_analysis")));
	}

	//
	protected void goToKnowledgeBase() throws InterruptedException {
		getDriver().get(baseUrl + "/KnowledgeBase");

		new WebDriverWait(getDriver(), 2)
				.until(ExpectedConditions.presenceOfElementLocated(By.id("section_profile_analysis")));
	}

	protected boolean isElementPresent(By by) {
		try {
			return findElement(by) != null;
		} catch (NoSuchElementException | TimeoutException e) {
			return false;
		}
	}

	protected void login(String username, String password) throws InterruptedException {
		new Login().testLogin(username, password);
	}

	protected void selectAnalysis(String analyseName) throws InterruptedException {
		selectCheckBox(true, By.xpath("//div[@id='section_analysis']//tbody//td[2 and string() = '" + analyseName
				+ "']/..//input[@type='checkbox']"));
	}

	protected void selectCheckBox(boolean stateToBecome, By by) throws InterruptedException {
		if (findElement(by).isSelected() != stateToBecome)
			click(by);
	}

	protected boolean sendKeys(WebElement element, String msg) {

		try {
			int count = 0;
			new WebDriverWait(getDriver(), 10).until(ExpectedConditions.not(ExpectedConditions.stalenessOf(element)));
			while (count < 10 && !getTextFromTextareaInput(element).equals(msg)) {
				continueClearingUntilClear(element);
				element.sendKeys(msg);
				count++;
			}
			return true;
		} catch (InvalidElementStateException elementStateException) {
			return false;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@AfterTest(alwaysRun = true)
	protected void signOut() throws InterruptedException {
		try {
			getDriver().get(baseUrl);
			click(By.id("main_menu_logout"));
		} catch (Exception e) {
			printError(e);
		}
	}

	protected boolean waitClick(By by) {
		try {
			new WebDriverWait(getDriver(), 10).until(ExpectedConditions.elementToBeClickable(by));
			return true;
		} catch (TimeoutException e) {
			return false;
		}
	}

	protected void waitLoadingIndicator() {
		new WebDriverWait(getDriver(), 30)
				.until(ExpectedConditions.invisibilityOfElementLocated(By.id("#loading-indicator")));
		assert !isElementPresent(By.id("#loading-indicator"));
	}
}
