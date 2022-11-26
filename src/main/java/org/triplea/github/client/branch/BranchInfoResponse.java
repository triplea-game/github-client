package org.triplea.github.client.branch;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents the data returned by github API for their 'branches' endpoint. This class Presents a
 * simplified interface for what is otherwise a JSON response.
 */
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BranchInfoResponse {
  @JsonProperty("commit")
  private LastCommit commit;

  /** Returns the date of the last commit. */
  public Instant getLastCommitDate() {
    return Instant.parse(commit.commit.author.date);
  }

  @ToString
  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  private static class LastCommit {

    private Commit commit;

    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    private static class Commit {
      private CommitDetails author;

      @ToString
      @AllArgsConstructor
      @NoArgsConstructor
      @Data
      private static class CommitDetails {
        private String date;
      }
    }
  }
}
