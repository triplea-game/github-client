package org.triplea.github.client;

import com.google.common.base.Preconditions;
import java.net.URI;
import java.util.Collection;
import java.util.Optional;
import org.triplea.github.client.branch.BranchInfoResponse;
import org.triplea.github.client.issue.CreateIssueRequest;
import org.triplea.github.client.issue.CreateIssueResponse;
import org.triplea.github.client.repo.RepositoryListing;

@SuppressWarnings("unused")
public interface GithubClient {
  CreateIssueResponse createIssue(CreateIssueRequest createIssueRequest);

  /**
   * Fetches details of a specific branch on a specific repo. Useful for retrieving info about the
   * last commit to the repo. Note, the repo listing contains a 'last_push' date, but this method
   * should be used instead as the last_push date on a repo can be for any branch (even PRs).
   *
   * <p>Example equivalent cUrl:
   * https://api.github.com/repos/triplea-maps/star_wars_galactic_war/branches/master
   *
   * @param branchName Which branch to be queried.
   * @return Payload response object representing the response from Github's web API.
   */
  BranchInfoResponse fetchBranchInfo(String repositoryName, String branchName);

  Collection<RepositoryListing> listRepositories();

  Optional<String> fetchLatestVersion(String repositoryName);

  static GithubClient buildClientForTripleaGame(String authToken) {
    Preconditions.checkNotNull(authToken);
    return ExternalGithubClient.builder()
        .uri(URI.create("https://api.github.com"))
        .authToken(authToken)
        .githubOrganization("triplea-game")
        .build();
  }

  static GithubClient buildClientForTripleaMaps(String authToken) {
    Preconditions.checkNotNull(authToken);
    return ExternalGithubClient.builder()
        .uri(URI.create("https://api.github.com"))
        .authToken(authToken)
        .githubOrganization("triplea-maps")
        .build();
  }
}
