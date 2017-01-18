package com.sabadell.bs_personal.internal.data;

import com.sabadell.bs_personal.BuildConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, manifest = Config.NONE, sdk = 23)
public final class PersistenceTest {
  private Persistence persistence;
  private static final String address = "address";

  @Before public void before() {
    persistence = new Persistence(RuntimeEnvironment.application);
  }

  @Test public void Verify_Persistence() {
    persistence.saveAddress(address);

    assertThat(persistence.getAddress(), is(address));

    persistence.removeAccount();
    assertNull(persistence.getAddress());
  }
}
