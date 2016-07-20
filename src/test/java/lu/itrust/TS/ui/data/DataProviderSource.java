package lu.itrust.TS.ui.data;

import java.lang.reflect.Method;

import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

public class DataProviderSource {
	@DataProvider(name = "dataProvider")
	public static Object[][] getTestTypeData(ITestContext context, Method method) {
		String testName = context.getName();
		String methodName = method.getName();

		if (methodName.equals("testLogin"))
			return new Object[][] { { "deimos", "Qwertz12" } };

		switch (testName) {
		case "firstInstallation":
			switch (methodName) {
			case "testRegister":
				return new Object[][] { { "deimos", "Qwertz12", "Qwertz12", "deimos", "alpha", "deimos@test.de", "en" } };
			case "testLogin":
				return new Object[][] { { "deimos", "Qwertz12" } };
			case "addUser":
				return new Object[][] { { "deimosa", "Qwertz12", "deimos", "alpha", "deimos.aplha@test.de", new String[] { "ROLE_CONSULTANT" } },
						{ "deimosb", "Qwertz12", "deimos", "beta", "deimos.beta@test.de", new String[] { "ROLE_USER" } },
						{ "deimosc", "Qwertz12", "deimos", "creos", "deimos.creos@test.de", new String[] { "ROLE_SUPERVISOR" } } };
			case "addCustomer":
				return new Object[][] { { "iTrust", "Mr D", "26123456", "itrust@test.de", "123, route", "lux", "1234", "Luxembourg" } };
			default:
				throw new RuntimeException("Test (" + testName + ") with method " + methodName + " is not supported");
			}
		case "AdministrationSettingCheckIfItWorks":
			return new Object[][] { { "deimos", "Qwertz12" } };
		case "CustomerAddEditDeleete":
			switch (methodName) {
			case "addCustomer":
				return new Object[][] { { "iTrusta", "mr tester", "1234", "tes.t@ya.de", "d", "gf", "d", "12 route" } };
			case "updateCustomer":
				return new Object[][] { { "iTrusta", "TrustB", "mr tester", "1234", "tes.t@ya.de", "d", "gf", "d", "12 route" } };
			case "deleteCustomer":
				return new Object[][] { { "TrustB" } };
			default:
				throw new RuntimeException("Test (" + testName + ") with method " + methodName + " is not supported");
			}
		case "languageAddEditDelete":
			switch (methodName) {
			case "addLanguage":
				return new Object[][] { { "GER", "Deutsch", "Allemand" } };
			case "updateLanguage":
				return new Object[][] { { "GER", "JPS", "JAPANESE", "NIPPON" } };
			case "deleteLanguage":
				return new Object[][] { { "JPS" } };
			default:
				throw new RuntimeException("Test (" + testName + ") with method " + methodName + " is not supported");
			}
		case "standardsAddEditDelete":
			switch (methodName) {
			case "addStandard":
				return new Object[][] { { "ISO 99999", "1", "This is a test", true, "NORMAL" } };
			case "updateStandard":
				return new Object[][] { { "ISO 99999", "ISO 999991", "2", "This is a testa", false, "MATURITY" } };
			case "deleteStandard":
				return new Object[][] { { "ISO 999991" } };
			default:
				throw new RuntimeException("Test (" + testName + ") with method " + methodName + " is not supported");
			}
		case "measuresAddEditDelete":
			switch (methodName) {
			case "addStandard":
				return new Object[][] { { "ISO 99999", "1", "This is a test", true, "NORMAL" } };

			case "addMeasure":
				return new Object[][] { { "ISO 99999", "Test", "1", true, "Test", "This is a testa" } };
			case "updateMeasure":
				return new Object[][] { { "ISO 99999", "Test", "Testa", "1", true, "Tesa", "This is a test" } };
			case "deleteMeasure":
				return new Object[][] { { "ISO 99999", "Testa" } };

			case "deleteStandard":
				return new Object[][] { { "ISO 99999" } };
			default:
				throw new RuntimeException("Test (" + testName + ") with method " + methodName + " is not supported");
			}
		case "AnalysisProfiles":
			switch (methodName) {
			case "addCustomer":
				return new Object[][] { { "testeranaprofile", "Mr D", "26123456", "itrust@test.de", "1234", "lux", "lux", "1234 route" } };
			case "newProfile":
				return new Object[][] { { "testeranaprofile", "Profile 1" } };
			case "analyseProfilesOpenProfile":
				return new Object[][] { { "Profile 1" } };
			case "deleteProfile":
				return new Object[][] { { true, "Profile 1" } };
			case "deleteCustomer":
				return new Object[][] { { "testeranaprofile" } };
			default:
				throw new RuntimeException("Test (" + testName + ") with method " + methodName + " is not supported");
			}
		case "Analyse":
			switch (methodName) {
			case "addLanguage":
				return new Object[][] { { "TES", "asdf", "as" } };
			case "addCustomer":
				return new Object[][] { { "tester", "Mr D", "26123456", "itrust@test.de", "1234", "lux", "lux", "1234 route" } };
			case "newProfile":
				return new Object[][] { { "tester", "profile1" } };
			case "addAnalysis":
				return new Object[][] { { "tester", "asdf", "profile1", "Deimos Chan", "0.0.2", "Analyse 1", "Tester", true, false } };
			case "testReadOnlyAnalysis":
			case "testAnalyse":
				return new Object[][] { { "tester", "Analyse 1" } };
			case "deleteAnalysis":
				return new Object[][] { { "profile1", "Analyse 1" } };

			case "deleteLanguage":
				return new Object[][] { { "TES" } };
			case "deleteCustomer":
				return new Object[][] { { "tester" } };
			case "deleteProfile":
				return new Object[][] { { true, "profile1" } };
			default:
				throw new RuntimeException("Test (" + testName + ") with method " + methodName + " is not supported");
			}
		case "checkingProfile":
			switch (methodName) {
			case "addCustomer":
				return new Object[][] { { "testerasdfgbf", "Mr D", "26123456", "itrust@test.de", "1234", "lux", "lux", "1234 route" } };
			case "newProfile":
				return new Object[][] { { "testerasdfgbf", "profileA" }, { "testerasdfgbf", "profileB" } };
			case "defaultsChecking":
				return new Object[][] { { "profileA", "profileB" } };
			case "deleteProfile":
				return new Object[][] { { true, "profileA" }, { true, "profileB" } };
			case "deleteCustomer":
				return new Object[][] { { "testerasdfgbf" } };
			default:
				throw new RuntimeException("Test (" + testName + ") with method " + methodName + " is not supported");
			}
		case "detailsProfilTest":
			switch (methodName) {
			case "addLanguage":
				return new Object[][] { { "TES", "asdf", "as" } };
			case "addCustomer":
				return new Object[][] { { "testerasdfghf", "Mr D", "26123456", "itrust@test.de", "1234", "lux", "lux", "1234 route" } };
			case "deleteLanguage":
				return new Object[][] { { "TES" } };
			case "newProfile":
				return new Object[][] { { "testerasdfghf", "profileAlpha" } };
			case "detailsProfile":
				return new Object[][] { { "profileAlpha", "123", "asdf" } };
			case "deleteProfile":
				return new Object[][] { { true, "profileAlpha" } };
			case "deleteCustomer":
				return new Object[][] { { "testerasdfghf" } };
			default:
				throw new RuntimeException("Test (" + testName + ") with method " + methodName + " is not supported");
			}
		case "detailsAnalyseTest":
			switch (methodName) {
			case "addLanguage":
				return new Object[][] { { "ABC", "japadsv", "lol" }, { "DEF", "kolasas", "qewdsds" } };
			case "addCustomer":
				return new Object[][] { { "testeralp", "Mr D", "26123456", "itrust@test.de", "1234", "lux", "lux", "1234 route" },
						{ "testeralpp", "Mr D", "26123456", "itruast@test.de", "1234", "lux", "lux", "1234 route" } };
			case "newProfile":
				return new Object[][] { { "testeralp", "profile2" } };
			case "addAnalysis":
				return new Object[][] { { "testeralp", "japadsv", "profile2", "Deimos Chan", "0.0.1", "Analyse 10", "Tester", true, false } };

			case "editAnalysis":
				return new Object[][] { { "testeralp", "Analyse 10", "LOL 10", "testeralpp", "kolasas", true, false } };

			case "deleteAnalysis":
				return new Object[][] { { "testeralpp", "LOL 10" } };

			case "deleteLanguage":
				return new Object[][] { { "ABC" }, { "DEF" } };
			case "deleteCustomer":
				return new Object[][] { { "testeralp" }, { "testeralpp" } };
			case "deleteProfile":
				return new Object[][] { { true, "profile2" } };
			default:
				throw new RuntimeException("Test (" + testName + ") with method " + methodName + " is not supported");
			}
		default:
			throw new RuntimeException("Test (" + testName + ") is not supported");
		}
	}
}
