package org.testory.common;

import static org.junit.Assert.assertArrayEquals;
import static org.testory.common.Samplers.fairSampler;
import static org.testory.common.Samplers.withSingleElementArray;

import org.junit.Before;
import org.junit.Test;

public class test_Samplers_withSingleElementArray {
  private String seed;
  private Sampler sampler, arraySampler;

  @Before
  public void before() {
    seed = "seed";
    sampler = fairSampler();
    arraySampler = withSingleElementArray(sampler);
  }

  @Test
  public void creates_array_of_samples() {
    assertArrayEquals(
        new String[] { sampler.sample(String.class, seed + "[0]") },
        arraySampler.sample(String[].class, seed));
  }

  @Test
  public void creates_deep_array_of_samples() {
    assertArrayEquals(
        new String[][] { { sampler.sample(String.class, seed + "[0][0]") } },
        arraySampler.sample(String[][].class, seed));
  }
}
