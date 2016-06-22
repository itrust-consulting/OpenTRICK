package lu.itrust.TS.ui.driver;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

public class LocalDriver extends AbstractDriver implements Driver {

	private static Driver singleton = null;

	private LocalDriver() {
	}

	public static Driver getInstance() {
		if (singleton == null) {
			synchronized (LocalDriver.class) {
				if (singleton == null)
					singleton = new LocalDriver();
			}
		}
		return singleton;
	}

	@Override
	protected final WebDriver buildInstance(String path) {
		FirefoxProfile profile = new FirefoxProfile();
		profile.setEnableNativeEvents(false);

		DesiredCapabilities capabilities = DesiredCapabilities.firefox();
		capabilities.setCapability("browser.cache.disk.enable", false);
		capabilities.setCapability("browser.cache.memory.enable", false);
		capabilities.setCapability("browser.cache.offline.enable", false);
		capabilities.setCapability("network.http.use-cache", false);

		WebDriver driver = new FirefoxDriver(new FirefoxBinary(new File(path)), profile, capabilities);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
		return this.driver = driver;
	}
}
