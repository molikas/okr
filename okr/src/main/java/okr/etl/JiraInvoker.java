package okr.etl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

/**
 * Abstract class to hold common methods for classes that need to formally have
 * access to Jira.
 * 
 * @author isidenica
 *
 */
public abstract class JiraInvoker {

	public static final Logger log = Logger.getLogger(JiraInvoker.class.getName());

	@Autowired
	private JiraAuthenticationProvider jAuthProvider;

	/**
	 * Constructs authorizes the system user to have access to Jira.
	 * There is no support for the current user in context at the moment.
	 * @return
	 */
	public JiraRestClient client() {
		JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
		try {
			URI jiraServerUri = new URI(jAuthProvider.getUrl());
			return factory.createWithBasicHttpAuthentication(jiraServerUri, jAuthProvider.getUsername(),
					jAuthProvider.getPassword());
		} catch (URISyntaxException e) {
			log.log(Level.WARNING, "REST Client init error:", e);
		}
		return null;
	}
}
