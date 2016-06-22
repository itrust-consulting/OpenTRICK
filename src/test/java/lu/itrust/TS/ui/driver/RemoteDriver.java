package lu.itrust.TS.ui.driver;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class RemoteDriver extends AbstractDriver {

	private static Driver singleton = null;

	private RemoteDriver() {
	}

	public static Driver getInstance() {
		if (singleton == null) {
			synchronized (LocalDriver.class) {
				if (singleton == null)
					singleton = new RemoteDriver();
			}
		}
		return singleton;
	}

	@Override
	protected WebDriver buildInstance(String url) {
		try {
			DesiredCapabilities capability = DesiredCapabilities.firefox();
			return driver = new RemoteWebDriver(new URL(url), capability);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

}
