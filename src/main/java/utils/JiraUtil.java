package utils;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

public class JiraUtil {
    private static final Logger logger = LoggerFactory.getLogger(JiraUtil.class);
    private final JiraRestClient restClient;
    private final String projectKey;
    private final boolean enabled;
    private final boolean stubMode;

    /**
     * Creates a JiraUtil. If jiraUrl or credentials are missing/empty, the utility
     * is disabled and will no-op.
     * It will also attempt to read missing values from environment variables:
     * JIRA_URL, JIRA_USER, JIRA_TOKEN, JIRA_PROJECT.
     */
    public JiraUtil(String jiraUrl, String username, String password, String projectKey) throws URISyntaxException {
        // Fallback to environment variables when properties are not provided
        if ((jiraUrl == null || jiraUrl.isBlank()) && System.getenv("JIRA_URL") != null) {
            jiraUrl = System.getenv("JIRA_URL");
        }
        if ((username == null || username.isBlank()) && System.getenv("JIRA_USER") != null) {
            username = System.getenv("JIRA_USER");
        }
        if ((password == null || password.isBlank()) && System.getenv("JIRA_TOKEN") != null) {
            password = System.getenv("JIRA_TOKEN");
        }
        if ((projectKey == null || projectKey.isBlank()) && System.getenv("JIRA_PROJECT") != null) {
            projectKey = System.getenv("JIRA_PROJECT");
        }

        this.projectKey = projectKey;

        // Test stub mode: allow special jiraUrl "STUB" or env var JIRA_STUB=true to
        // enable a no-network stub
        boolean envStub = "true".equalsIgnoreCase(System.getenv("JIRA_STUB"));
        if (envStub || (jiraUrl != null && "STUB".equalsIgnoreCase(jiraUrl))) {
            this.restClient = null;
            this.enabled = true; // enabled but stubbed
            this.stubMode = true;
            return;
        }

        if (jiraUrl == null || jiraUrl.isBlank() || username == null || username.isBlank() || password == null
                || password.isBlank() || projectKey == null || projectKey.isBlank()) {
            // JIRA not configured — disable calls to JIRA to avoid runtime failures
            this.restClient = null;
            this.enabled = false;
            this.stubMode = false;
            return;
        }

        AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        this.restClient = factory.createWithBasicHttpAuthentication(new URI(jiraUrl), username, password);
        this.enabled = true;
        this.stubMode = false;
    }

    /**
     * Returns true if JIRA client is configured and enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    public String createIssue(String summary, String description) {
        if (!enabled) {
            // Integration not configured — return null to indicate no issue was created.
            return null;
        }

        if (stubMode) {
            // Return a deterministic stub key for tests
            return "STUB-1";
        }

        IssueInput newIssue = new IssueInputBuilder(projectKey, 1L, summary) // 1L = Bug
                .setDescription(description)
                .build();
        try {
            BasicIssue issue = restClient.getIssueClient().createIssue(newIssue).get();
            return issue.getKey();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Failed to create Jira issue for project: {}", projectKey, e);
            return null;
        }
    }

    public void close() throws Exception {
        if (restClient != null) {
            restClient.close();
        }
    }
}
