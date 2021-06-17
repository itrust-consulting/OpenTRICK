package lu.itrust.TS.ui.driver;

import org.testng.annotations.Parameters;

public class DriverBuilder {

	@Parameters(value = { "driver.type" })
	public synchronized static Driver getInstanceDriver(DriverType type) {
		assert type != null;
		switch (type) {
		case REMOTE:
			return RemoteDriver.getInstance();
		default:
			throw new RuntimeException("Driver not supported");
		}
	}

}
