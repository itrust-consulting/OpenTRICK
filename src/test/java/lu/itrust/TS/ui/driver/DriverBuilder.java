package lu.itrust.TS.ui.driver;

public class DriverBuilder {

	public static Driver getInstanceDriver(DriverType type) {
		switch (type) {
		case LOCAL:
			return LocalDriver.getInstance();
		case REMOTE:
			return RemoteDriver.getInstance();
		default:
			throw new RuntimeException("Driver not supported");
		}
	}

}
