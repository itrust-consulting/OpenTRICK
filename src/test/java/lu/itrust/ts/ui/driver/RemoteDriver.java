package lu.itrust.ts.ui.driver;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class RemoteDriver extends AbstractDriver {

	private static Driver singleton = null;

	public static Driver getInstance() {
		if (singleton == null) {
			synchronized (RemoteDriver.class) {
				if (singleton == null)
					singleton = new RemoteDriver();
			}
		}
		return singleton;
	}

	private RemoteDriver() {
	}

	@Override
	protected WebDriver createInstance(DesiredCapabilities capabilities, String url) {
		try {
			return new RemoteWebDriver(new URL(url), capabilities);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

}
