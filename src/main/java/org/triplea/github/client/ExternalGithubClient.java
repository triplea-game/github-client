package org.triplea.github.client;

import com.google.common.base.Preconditions;
import feign.Feign;
import feign.FeignException;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.triplea.github.client.branch.BranchInfoResponse;
import org.triplea.github.client.issue.CreateIssueRequest;
import org.triplea.github.client.issue.CreateIssueResponse;
import org.triplea.github.client.repo.RepositoryListing;

/** Can be used to interact with Github's webservice API. */
@Slf4j
class ExternalGithubClient implements GithubClient {
  private final ExternalGithubService githubApiFeignClient;

  @Getter private final String githubOrganization;

  /**
   * @param uri The URI for githubs webservice API.
   * @param authToken Auth token that will be sent to Github for webservice calls. Can be empty, but
   *     if specified must be valid (no auth token still works, but rate limits will be more
   *     restrictive).
   * @param githubOrganization Name of the github org to be queried.
   */
  @Builder
  public ExternalGithubClient(
      @Nonnull URI uri, @Nullable String authToken, @Nonnull String githubOrganization) {
    githubApiFeignClient =
        Feign.builder()
            .encoder(new JacksonEncoder())
            .decoder(new JacksonDecoder())
            .requestInterceptor(
                requestTemplate -> {
                  requestTemplate.header("Content-Type", "application/json");
                  requestTemplate.header("Accept", "application/json");
                  if (authToken != null) {
                    requestTemplate.header("Authorization", "token " + authToken);
                  }
                })
            .target(ExternalGithubService.class, uri.toString());
    this.githubOrganization = githubOrganization;
  }

  /**
   * Invokes Github web-API to create a Github issue with the provided parameter data.
   *
   * @param createIssueRequest Upload data for creating the body and title of the github issue.
   * @return Response from server containing link to the newly created issue.
   * @throws feign.FeignException thrown on error or if non-2xx response is received
   */
  @Override
  public CreateIssueResponse createIssue(CreateIssueRequest createIssueRequest) {
    Preconditions.checkNotNull(createIssueRequest);
    return githubApiFeignClient.newIssue(
        githubOrganization, createIssueRequest.getRepo(), createIssueRequest);
  }

  /**
   * Returns a listing of the repositories within a Github organization. This call handles paging,
   * it returns a complete list and may perform multiple calls to Github.
   *
   * <p>Example equivalent cUrl call:
   *
   * <p>curl https://api.github.com/orgs/triplea-maps/repos
   */
  @Override
  public Collection<RepositoryListing> listRepositories() {
    final Collection<RepositoryListing> allRepos = new HashSet<>();
    int pageNumber = 1;
    Collection<RepositoryListing> repos = listRepositories(pageNumber);
    while (!repos.isEmpty()) {
      pageNumber++;
      allRepos.addAll(repos);
      repos = listRepositories(pageNumber);
    }
    return allRepos;
  }

  private Collection<RepositoryListing> listRepositories(int pageNumber) {
    final Map<String, String> queryParams = new HashMap<>();
    queryParams.put("per_page", "100");
    queryParams.put("page", String.valueOf(pageNumber));

    return githubApiFeignClient.listRepos(queryParams, githubOrganization);
  }

  @Override
  public BranchInfoResponse fetchBranchInfo(String repoName, String branchName) {
    Preconditions.checkNotNull(repoName);
    Preconditions.checkNotNull(branchName);
    return githubApiFeignClient.getBranchInfo(githubOrganization, repoName, branchName);
  }

  @Override
  public Optional<String> fetchLatestVersion(String repoName) {
    Preconditions.checkNotNull(repoName);
    try {
      return Optional.of(
          githubApiFeignClient.getLatestRelease(githubOrganization, repoName).getTagName());
    } catch (final FeignException e) {
      log.error("No data received from server for latest engine version", e);
      return Optional.empty();
    }
  }
}
