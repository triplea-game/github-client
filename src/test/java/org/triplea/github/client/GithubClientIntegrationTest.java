package org.triplea.github.client;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;

import java.net.URI;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.triplea.github.client.issue.CreateIssueRequest;

/** Sends live requests to Github to validate integration. */
class GithubClientIntegrationTest {
  @Test
  void repoListing() {
    var client =
        ExternalGithubClient.builder()
            .uri(URI.create("https://api.github.com"))
            .githubOrganization("triplea-maps")
            .build();
    assertThat(client.listRepositories(), is(not(empty())));
  }

  @Test
  void fetchBranchInfo() {
    var client =
        ExternalGithubClient.builder()
            .uri(URI.create("https://api.github.com"))
            .githubOrganization("triplea-maps")
            .build();
    assertThat(
        client.fetchBranchInfo("test-map", "master").getLastCommitDate(), is(notNullValue()));
  }

  @Test
  void fetchLatestVersion() {
    var client =
        ExternalGithubClient.builder()
            .uri(URI.create("https://api.github.com"))
            .githubOrganization("triplea-game")
            .build();
    assertThat(client.fetchLatestVersion("triplea"), isPresent());
  }

  /**
   * Live test to create an issue. Disabled for a few reasons:<br>
   * 1) requires a live authentication token <br>
   * 2) would require extra code to cleanup issues
   */
  @Disabled
  @Test
  void createIssue() {
    var client =
        ExternalGithubClient.builder()
            .uri(URI.create("https://api.github.com"))
            .githubOrganization("triplea-game")
            .authToken("[ADD YOUR TOKEN HERE]")
            .build();
    client.createIssue(
        CreateIssueRequest.builder()
            .title("Test Issue")
            .repo("test")
            .body("example content")
            .labels(new String[] {"Error Report"})
            .build());
  }
}
