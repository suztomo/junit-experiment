package example;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class SimpleTest {

  @Test
  public void simpleTest() {
    String name = "my name";
    assertThat(name).isEqualTo("my name");
  }
}