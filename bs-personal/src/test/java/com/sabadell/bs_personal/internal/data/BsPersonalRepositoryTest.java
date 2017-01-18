package com.sabadell.bs_personal.internal.data;

import com.sabadell.bs_personal.BsPersonal;
import com.sabadell.bs_personal.BuildConfig;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import retrofit2.Response;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, manifest = Config.NONE, sdk = 23)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class BsPersonalRepositoryTest {
  private static BsPersonal repositoryUT;
  private static final String PASS = "password", ADDRESS = "address";

  @Before public void before() {
    //can't use BeforeClass because Robolectric init clashes.
    if (repositoryUT != null) return;
    repositoryUT = new BsPersonal.Builder().build(RuntimeEnvironment.application);
  }

  @Test public void _a01_Verify_Create_Account_With_Empty_Password() {
    repositoryUT.createAccount("")
        .test()
        .assertNoValues()
        .assertErrorMessage(BsPersonalRepository.PASSWORD_VALIDATION)
        .assertNotComplete();

    repositoryUT.createAccount(null)
        .test()
        .assertNoValues()
        .assertErrorMessage(BsPersonalRepository.PASSWORD_VALIDATION)
        .assertNotComplete();
  }

  @Test public void _a02_Verify_Create_Account() {
    repositoryUT.createAccount(PASS)
        .test()
        .assertNoErrors()
        .assertValueCount(1)
        .assertComplete();
  }

  @Test public void _a03_Verify_Create_Account_With_An_Open_Session() {
    repositoryUT.createAccount(PASS)
        .test()
        .assertNoValues()
        .assertErrorMessage(BsPersonalRepository.ACTIVE_SESSION)
        .assertNotComplete();
  }

  @Test public void _a04_Verify_Login_With_An_Open_Session() {
    repositoryUT.login(ADDRESS)
        .test()
        .assertNoValues()
        .assertErrorMessage(BsPersonalRepository.ACTIVE_SESSION)
        .assertNotComplete();
  }

  @Test public void _a05_Verify_Destroy_Session_With_An_Open_Session() {
    repositoryUT.destroySession()
        .test()
        .assertNoErrors()
        .assertValueCount(1)
        .assertComplete();
  }

  @Test public void _a06_Verify_Login_With_Invalid_Address() {
    repositoryUT.login("")
        .test()
        .assertNoValues()
        .assertErrorMessage(BsPersonalRepository.ADDRESS_VALIDATION)
        .assertNotComplete();

    repositoryUT.login(null)
        .test()
        .assertNoValues()
        .assertErrorMessage(BsPersonalRepository.ADDRESS_VALIDATION)
        .assertNotComplete();
  }

  @Test public void _a07_Verify_Login() {
    repositoryUT.login(ADDRESS)
        .test()
        .assertNoErrors()
        .assertValueCount(1)
        .assertComplete();
  }

  @Test public void _a08_Verify_Active_Session_Active() {
    repositoryUT.activeSession()
        .test()
        .assertValue(true)
        .assertValueCount(1)
        .assertNoErrors()
        .assertComplete();
  }

  @Test public void _a09_Verify_Get_Address_With_An_Open_Session() {
    repositoryUT.getAddress()
        .test()
        .assertValueCount(1)
        .assertNoErrors()
        .assertComplete();
  }

  @Test public void _b01_Verify_Destroy_Session_With_An_Open_Session() {
    repositoryUT.destroySession()
        .test()
        .assertNoErrors()
        .assertValueCount(1)
        .assertComplete();
  }

  @Test public void _b02_Verify_Active_Session_Inactive() {
    repositoryUT.activeSession()
        .test()
        .assertValue(false)
        .assertValueCount(1)
        .assertNoErrors()
        .assertComplete();
  }

  @Test public void _b03_Verify_Destroy_Session_Without_An_Open_Session() {
    repositoryUT.destroySession()
        .test()
        .assertNoValues()
        .assertErrorMessage(BsPersonalRepository.NOT_ACTIVE_SESSION)
        .assertNotComplete();
  }

  @Test public void _b04_Verify_Get_Address_Without_An_Open_Session() {
    repositoryUT.getAddress()
        .test()
        .assertNoValues()
        .assertErrorMessage(BsPersonalRepository.NOT_ACTIVE_SESSION)
        .assertNotComplete();
  }

  private <T> Observable<Response<T>> mockErrorResponse(String message) {
    Response<T> response = Response.error(404, ResponseBody.create(null, message));
    return Observable.just(response);
  }
}
