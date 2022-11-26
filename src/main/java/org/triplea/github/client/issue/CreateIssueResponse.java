package org.triplea.github.client.issue;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Response JSON object from github after we create a new issue. */
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateIssueResponse {

  @JsonProperty("html_url")
  private String htmlUrl;
}
