package com.sabadell.bs_personal.internal.data;

import com.sabadell.bs_personal.internal.net.BsPersonalApi;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import retrofit2.Response;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class BsPersonalRepositoryTest {
  @Mock Persistence persistence;
  @Mock BsPersonalApi api;
  @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
  private BsPersonalRepository repositoryUT;
  private static final String PASS = "password", ADDRESS = "address";


  @Before public void before() {
    repositoryUT = new BsPersonalRepository(persistence, api);
  }

  @Test public void Verify_Create_Account_With_Empty_Password() {
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

  @Test public void Verify_Create_Account_With_An_Open_Session() {
    when(persistence.getAddress()).thenReturn(ADDRESS);

    repositoryUT.createAccount(PASS)
        .test()
        .assertNoValues()
        .assertErrorMessage(BsPersonalRepository.ACTIVE_SESSION)
        .assertNotComplete();
  }

  @Test public void Verify_Create_Account_Success() {
    when(api.createSession(PASS)).thenReturn(
        Observable.just(Response.success(new Address(ADDRESS))));

    repositoryUT.createAccount(PASS)
        .test()
        .assertNoErrors()
        .assertValueCount(1)
        .assertComplete();

    verify(persistence, times(1)).saveAddress(ADDRESS);
  }

  @Test public void Verify_Login_With_Invalid_Address() {
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

  @Test public void Verify_Login_With_An_Open_Session() {
    when(persistence.getAddress()).thenReturn(ADDRESS);

    repositoryUT.login(ADDRESS)
        .test()
        .assertNoValues()
        .assertErrorMessage(BsPersonalRepository.ACTIVE_SESSION)
        .assertNotComplete();
  }

  @Test public void Verify_Login_Success() {
    repositoryUT.login(ADDRESS)
        .test()
        .assertNoErrors()
        .assertValueCount(1)
        .assertComplete();

    verify(persistence, times(1)).saveAddress(ADDRESS);
  }

  @Test public void Verify_Create_Account_Failure() {
    String errorMessage = "Can't create address";

    when(api.createSession(PASS)).thenReturn(this.<Address>mockErrorResponse(errorMessage));

    repositoryUT.createAccount(PASS)
        .test()
        .assertNoValues()
        .assertErrorMessage(errorMessage)
        .assertNotComplete();
  }

  @Test public void Verify_Active_Session_Active() {
    when(persistence.getAddress()).thenReturn(ADDRESS);

    repositoryUT.activeSession()
        .test()
        .assertValue(true)
        .assertValueCount(1)
        .assertNoErrors()
        .assertComplete();
  }

  @Test public void Verify_Active_Session_Inactive() {
    repositoryUT.activeSession()
        .test()
        .assertValue(false)
        .assertValueCount(1)
        .assertNoErrors()
        .assertComplete();
  }

  @Test public void Verify_Destroy_Session_With_An_Open_Session() {
    when(persistence.getAddress()).thenReturn(ADDRESS);

    repositoryUT.destroySession()
        .test()
        .assertNoErrors()
        .assertValueCount(1)
        .assertComplete();

    verify(persistence, times(1)).removeAccount();
  }

  @Test public void Verify_Destroy_Session_Without_An_Open_Session() {
    repositoryUT.destroySession()
        .test()
        .assertNoValues()
        .assertErrorMessage(BsPersonalRepository.NOT_ACTIVE_SESSION)
        .assertNotComplete();
  }

  @Test public void Verify_Get_Address_With_An_Open_Session() {
    String address = "address";
    when(persistence.getAddress()).thenReturn(address);

    repositoryUT.getAddress()
        .test()
        .assertValue(address)
        .assertValueCount(1)
        .assertNoErrors()
        .assertComplete();
  }

  @Test public void Verify_Get_Address_Without_An_Open_Session() {
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
