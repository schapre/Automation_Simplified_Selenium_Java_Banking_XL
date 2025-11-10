package utils;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test to validate JiraUtil stub mode. No external network calls.
 */
public class JiraStubbedTest {

    @Test
    public void createIssue_stubMode_returnsStubKey() throws Exception {
        // Use STUB url to trigger stub mode
        JiraUtil util = new JiraUtil("STUB", null, null, "MOCK");
        try {
            Assert.assertTrue(util.isEnabled(), "JiraUtil should be enabled in stub mode");
            String key = util.createIssue("s", "d");
            Assert.assertEquals(key, "STUB-1");
        } finally {
            util.close();
        }
    }
}
