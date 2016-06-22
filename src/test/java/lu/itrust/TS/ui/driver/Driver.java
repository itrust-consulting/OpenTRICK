package lu.itrust.TS.ui.driver;

import org.openqa.selenium.WebDriver;

public interface Driver {

	WebDriver getDriver(String path);

	void close() throws InterruptedException;
}