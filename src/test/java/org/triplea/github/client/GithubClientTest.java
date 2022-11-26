package org.triplea.github.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.triplea.github.client.branch.BranchInfoResponse;
import org.triplea.github.client.issue.CreateIssueRequest;
import org.triplea.github.client.issue.CreateIssueResponse;
import org.triplea.github.client.repo.RepositoryListing;
import ru.lanwen.wiremock.ext.WiremockResolver;
import ru.lanwen.wiremock.ext.WiremockUriResolver;

/**
 * Sends requests to a local wiremock server that responds with sampole response JSONs. This test
 * validates we can process these JSONs in an expected manner.
 */
@ExtendWith({WiremockResolver.class, WiremockUriResolver.class})
class GithubClientTest {

  @Test
  void repoListing(@WiremockResolver.Wiremock final WireMockServer server) {
    stubRepoListingResponse(
        1,
        server,
        TestDataFileReader.readContents("sample_responses/repo_listing_response_page1.json"));
    stubRepoListingResponse(
        2,
        server,
        TestDataFileReader.readContents("sample_responses/repo_listing_response_page2.json"));
    stubRepoListingResponse(3, server, "[]");

    final Collection<RepositoryListing> repos =
        ExternalGithubClient.builder()
            .githubOrganization("example-org")
            .uri(URI.create(server.baseUrl()))
            .build()
            .listRepositories();

    assertThat(repos, hasSize(3));
    assertThat(
        repos,
        hasItem(
            RepositoryListing.builder()
                .htmlUrl("https://github.com/triplea-maps/tutorial")
                .name("tutorial")
                .build()));
    assertThat(
        repos,
        hasItem(
            RepositoryListing.builder()
                .htmlUrl("https://github.com/triplea-maps/aa_enhanced_revised")
                .name("aa_enhanced_revised")
                .build()));
    assertThat(
        repos,
        hasItem(
            RepositoryListing.builder()
                .htmlUrl("https://github.com/triplea-maps/roman_invasion")
                .name("roman_invasion")
                .build()));
  }

  private void stubRepoListingResponse(
      final int expectedPageNumber, final WireMockServer server, final String response) {
    server.stubFor(
        get("/orgs/example-org/repos?per_page=100&page=" + expectedPageNumber)
            .willReturn(aResponse().withStatus(200).withBody(response)));
  }

  @Test
  @DisplayName("Invoke branches API and verify we can retrieve last commit date")
  void branchListingResponseFetchLastCommitDate(
      @WiremockResolver.Wiremock final WireMockServer server) {
    final String exampleResponse =
        TestDataFileReader.readContents("sample_responses/branch_listing_response.json");
    server.stubFor(
        get("/repos/example-org/map-repo/branches/master")
            .withHeader("Authorization", equalTo("token test-token"))
            .willReturn(aResponse().withStatus(200).withBody(exampleResponse)));

    final BranchInfoResponse branchInfoResponse =
        ExternalGithubClient.builder()
            .authToken("test-token")
            .githubOrganization("example-org")
            .uri(URI.create(server.baseUrl()))
            .build()
            .fetchBranchInfo("map-repo", "master");

    final Instant expectedLastCommitDate =
        LocalDateTime.of(2021, 2, 4, 19, 30, 32).atOffset(ZoneOffset.UTC).toInstant();
    assertThat(branchInfoResponse.getLastCommitDate(), is(expectedLastCommitDate));
  }

  @Test
  void getLatestRelease(@WiremockResolver.Wiremock final WireMockServer server) {
    final String exampleResponse =
        TestDataFileReader.readContents("sample_responses/latest_release_response.json");
    server.stubFor(
        get("/repos/example-org/map-repo/releases/latest")
            .withHeader("Authorization", equalTo("token test-token"))
            .willReturn(aResponse().withStatus(200).withBody(exampleResponse)));

    final String latestVersion =
        ExternalGithubClient.builder()
            .authToken("test-token")
            .githubOrganization("example-org")
            .uri(URI.create(server.baseUrl()))
            .build()
            .fetchLatestVersion("map-repo")
            .orElseThrow();

    assertThat(latestVersion, is("2.5.22294"));
  }

  @Test
  void createIssue(@WiremockResolver.Wiremock final WireMockServer server) {

    server.stubFor(
        post("/repos/example-org/test/issues")
            .withHeader("Authorization", equalTo("token test-token"))
            .withRequestBody(
                equalToJson(TestDataFileReader.readContents("sample_requests/create_issue.json")))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withBody(
                        TestDataFileReader.readContents(
                            "sample_responses/create_issue_response.json"))));

    final CreateIssueResponse response =
        ExternalGithubClient.builder()
            .authToken("test-token")
            .githubOrganization("example-org")
            .uri(URI.create(server.baseUrl()))
            .build()
            .createIssue(
                CreateIssueRequest.builder()
                    .title("Test Issue")
                    .repo("test")
                    .body("example content")
                    .labels(new String[] {"Error Report"})
                    .build());
    assertThat(response.getHtmlUrl(), is("https://github.com/triplea-game/test/issues/65"));
  }
}
