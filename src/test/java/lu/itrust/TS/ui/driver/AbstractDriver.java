/**
 * 
 */
package lu.itrust.TS.ui.driver;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author deimos
 *
 */
public abstract class AbstractDriver implements Driver {

	protected WebDriver driver = null;

	/**
	 * create instance webdriver
	 * 
	 * @param path
	 *            url or filepath
	 * @return
	 */
	protected abstract WebDriver buildInstance(String path);

	@Override
	public WebDriver getDriver(String path) {
		if (driver == null) {
			synchronized (this) {
				if (driver == null)
					return buildInstance(path);
			}
		}
		new WebDriverWait(driver, 30).until((ExpectedCondition<Boolean>) wd -> ((JavascriptExecutor) wd)
				.executeScript("return document.readyState").equals("complete"));
		return driver;
	}

	@Override
	public void close() throws InterruptedException {
		if (driver != null) {
			synchronized (driver) {
				if (driver != null) {
					Thread.sleep(1000);
					driver.close();
					driver.quit();
					driver = null;
				}
			}
		}
	}

}
