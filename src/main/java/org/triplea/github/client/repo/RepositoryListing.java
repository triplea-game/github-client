package org.triplea.github.client.repo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Response object from Github listing the details of an organization's repositories. */
@ToString
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@NoArgsConstructor
public class RepositoryListing {

  @JsonProperty("html_url")
  String htmlUrl;

  @Getter String name;

  public URI getUri() {
    return URI.create(htmlUrl);
  }
}
