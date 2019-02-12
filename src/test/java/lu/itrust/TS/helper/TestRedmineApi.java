/**
 * 
 */
package lu.itrust.TS.helper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.taskadapter.redmineapi.Params;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.internal.ResultsWrapper;

import lu.itrust.business.TS.model.ticketing.TicketingProject;
import lu.itrust.business.TS.model.ticketing.builder.redmine.RedmineClient;

/**
 * @author eomar
 *
 */
public class TestRedmineApi {

	@Test
	public void testConnection() throws IOException {
		RedmineClient client = new RedmineClient();
		Assert.assertTrue("Client is not enabled to connect redmine",
				client.connect("http://localhost:3000", "ts-tester", "strong-password-87564"));
		List<TicketingProject> projects = client.findProjects();
		Assert.assertNotNull("Projects cannot be null", projects);
		Assert.assertNotEquals("Project cannot be empty list", Collections.emptyList(), projects);
		client.close();
	}
	
	@Test
	public void testSearch() {
		final RedmineManager manager = RedmineManagerFactory.createWithUserAuth("http://localhost:3000", "ts-tester",
				"strong-password-87564");
		try {
			final Params parameters = new Params();
			parameters.add("f[]", "issue_id").add("op[issue_id]", "!").add("v[issue_id]", "").add("issue_id", "91,92").add("project_id", "1").add("status_id", "*");
			ResultsWrapper<Issue> results = manager.getIssueManager().getIssues(parameters);
			results.getResults().forEach(c -> System.out.println(c));
		} catch (RedmineException e) {
			e.printStackTrace();
		}
	}

}
