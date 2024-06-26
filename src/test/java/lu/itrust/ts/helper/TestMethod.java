package lu.itrust.ts.helper;

import static org.springframework.test.util.AssertionErrors.assertTrue;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

/**
 * @author eomar
 *
 */
public class TestMethod {

	public static ResultMatcher redirectedUrlMatch(final String url) {
		return new ResultMatcher() {
			@Override
			public void match(MvcResult result) throws Exception {
				if (url == null && result.getResponse().getRedirectedUrl() == null)
					return;
				if (url == null)
					throw new AssertionError("Redirection url not null: " + result.getResponse().getRedirectedUrl());
				if (result.getResponse().getRedirectedUrl() == null)
					throw new AssertionError("Redirection url is null");
				assertTrue("Bad redirection", result.getResponse().getRedirectedUrl().matches(url));
			}
		};
	}

}
