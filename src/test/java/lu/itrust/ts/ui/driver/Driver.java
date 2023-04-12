package lu.itrust.ts.ui.driver;

import org.openqa.selenium.WebDriver;

public interface Driver {

	void close() throws InterruptedException;

	WebDriver getDriver(String path);

	boolean isSkipTests();

	void setSkipTests(boolean skipTests);
}