package lu.itrust.TS.UI;

import java.net.URL;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class UI_Initialisation {

	protected WebDriver driver;

	@BeforeMethod(groups = "init")
	public void setUp() throws Exception {
		DesiredCapabilities capability = DesiredCapabilities.firefox();
		driver = new RemoteWebDriver(new URL("http://localhost:32768/wd/hub"), capability);
	}

}
