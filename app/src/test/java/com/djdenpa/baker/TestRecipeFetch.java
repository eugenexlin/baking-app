package com.djdenpa.baker;

import com.djdenpa.baker.core.network.RecipeFetcher;

import org.junit.Test;

/**
 * Created by denpa on 10/15/2017.
 *
 * tiny test
 */

public class TestRecipeFetch {
  @Test
  public void testNoErrorsFetching() throws Exception {
    RecipeFetcher.testGetRecipeList();
  }
}
