package org.triplea.github.client.repo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LatestReleaseResponse {
  @JsonProperty("tag_name")
  String tagName;
}
