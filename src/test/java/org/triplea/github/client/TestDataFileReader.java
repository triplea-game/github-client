package org.triplea.github.client;

import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestDataFileReader {

  /**
   * Reads file contents, file is expected to be in "src/test/resources".
   *
   * @param filePath Path to the file relative to 'src/test/resources'
   * @return Contents of the file read.
   * @throws TestDataFileNotFound Thrown if file does not exist or could not be read.
   */
  public static String readContents(final String filePath) {
    final ClassLoader classLoader = TestDataFileReader.class.getClassLoader();
    try (InputStream inputStream = classLoader.getResourceAsStream(filePath)) {
      if (inputStream == null) {
        throw new TestDataFileNotFound(filePath);
      }
      return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new TestDataFileNotFound(filePath, e);
    }
  }

  @VisibleForTesting
  static class TestDataFileNotFound extends RuntimeException {
    private static final long serialVersionUID = 6122387967083038888L;

    TestDataFileNotFound(final String filePath) {
      super("Failed to find file: " + filePath);
    }

    TestDataFileNotFound(final String filePath, Exception e) {
      super("Failed to find file: " + filePath, e);
    }
  }
}
