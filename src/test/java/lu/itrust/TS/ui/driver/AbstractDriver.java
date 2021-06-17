/**
 * 
 */
package lu.itrust.TS.ui.driver;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author deimos
 *
 */
public abstract class AbstractDriver implements Driver {

	private boolean skipTests = false;

	protected WebDriver driver = null;

	@Override
	public void close() throws InterruptedException {
		if (this.driver != null) {
			synchronized (this) {
				if (driver != null) {
					driver.quit();
					driver = null;
				}
			}
		}
	}

	@Override
	public WebDriver getDriver(String path) {
		if (driver == null) {
			synchronized (this) {
				if (driver == null)
					return build(path);
			}
		}
		new WebDriverWait(driver, 30).until((ExpectedCondition<Boolean>) wd -> ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));
		return driver;
	}

	public boolean isSkipTests() {
		return skipTests;
	}

	public void setSkipTests(boolean skipTests) {
		this.skipTests = skipTests;
	}

	/**
	 * create instance webdriver
	 * 
	 * @param path
	 *            url or filepath
	 * @return
	 */
	protected WebDriver build(String path) {
		assert !skipTests;
		DesiredCapabilities capabilities = DesiredCapabilities.firefox();
		capabilities.setCapability("browser.cache.disk.enable", false);
		capabilities.setCapability("browser.cache.memory.enable", false);
		capabilities.setCapability("browser.cache.offline.enable", false);
		capabilities.setCapability("network.http.use-cache", false);
		capabilities.setCapability("nativeEvents", false);
		capabilities.setCapability("marionette", false);

		WebDriver driver = createInstance(capabilities, path);
		driver.manage().deleteAllCookies();
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
		return this.driver = driver;

	}

	protected abstract WebDriver createInstance(DesiredCapabilities capabilities, String path);

}
