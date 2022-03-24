package com.example;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;

@RunWith(ParallelParameterized.class)
public class SimpleTest {

  private static final Logger LOGGER = Logger.getLogger(SimpleTest.class.getName());

  String myInput;
  String myExpected;

  public SimpleTest(String input, String expected) {
    myInput = input;
    myExpected=  expected;
  }

  @Test
  public void testMessage(){
    assertThat(myInput).isEqualTo(myExpected);
  }

  @Parameters(name = "{0}")
  public static Collection<Object[]> simpleTest() {
    return ImmutableList.of(new String[]{"ans", "ans"});
  }
}