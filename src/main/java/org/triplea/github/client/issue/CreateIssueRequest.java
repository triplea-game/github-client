package org.triplea.github.client.issue;

import com.google.common.base.Ascii;
import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Represents request data to create a github issue. */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateIssueRequest {
  /** Max length for Github issue title text. */
  private static final int TITLE_MAX_LENGTH = 125;

  /** Max length for Github issue body text. */
  private static final int REPORT_BODY_MAX_LENGTH = 65536;

  @Nonnull private String repo;
  @Nonnull private String title;
  @Nonnull private String body;

  private String[] labels;

  public String getTitle() {
    return Ascii.truncate(title, TITLE_MAX_LENGTH, "...");
  }

  public String getBody() {
    return Ascii.truncate(body, REPORT_BODY_MAX_LENGTH, "...");
  }
}
