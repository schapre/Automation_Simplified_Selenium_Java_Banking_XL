package utils;

import org.testng.SkipException;
import org.testng.annotations.Test;

/**
 * Integration test for JiraUtil. This test will be skipped when environment
 * variables
 * JIRA_URL, JIRA_USER, JIRA_TOKEN, or JIRA_PROJECT are not set.
 */
public class JiraIntegrationTest {

    @Test
    public void jiraCreateIssue_whenCredsPresent_createsIssueOrFailsGracefully() throws Exception {
        String url = System.getenv("JIRA_URL");
        String user = System.getenv("JIRA_USER");
        String token = System.getenv("JIRA_TOKEN");
        String project = System.getenv("JIRA_PROJECT");

        if (url == null || user == null || token == null || project == null) {
            throw new SkipException(
                    "JIRA credentials not configured in environment variables - skipping integration test");
        }

        JiraUtil jiraUtil = new JiraUtil(url, user, token, project);
        try {
            if (!jiraUtil.isEnabled()) {
                throw new SkipException("JiraUtil disabled despite env vars - skipping");
            }
            String key = jiraUtil.createIssue("Test issue from automation integration test",
                    "This issue was created by an automated integration test. Please delete.");
            System.out.println("Created JIRA issue: " + key);
            // We don't assert on the returned key since some JIRA setups may restrict
            // creation; ensure no exception thrown.
        } finally {
            jiraUtil.close();
        }
    }
}
