package com.sabadell.bs_personal;

import android.content.Context;
import android.support.annotation.CheckResult;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.sabadell.bs_personal.internal.data.BsPersonalRepository;
import com.sabadell.bs_personal.internal.data.Persistence;
import com.sabadell.bs_personal.internal.net.BsPersonalApi;
import io.reactivex.Observable;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Interface to operate with BSTokens services.
 */
public interface BsPersonal {

  /**
   * Create an account specifying a password. Calling this method opens an active session, if this
   * method is called again without calling previously {@link #destroySession()}, a RuntimeException
   * is thrown.
   *
   * @param password the password required for future operations.
   * @return the id-address associated with the brand new account.
   */
  @CheckResult Observable<String> createAccount(String password);

  /**
   * Calling this method open a session with the specified address. Calling this method opens an
   * active session, if this method is called again without calling previously {@link
   * #destroySession()}, a RuntimeException is thrown.
   *
   * @return Ignore is just a flag to avoid nullable types.
   */
  @CheckResult Observable<Ignore> login(String address);

  /**
   * Calling this method destroy the current session. If there is no active session, a
   * RuntimeException is thrown.
   *
   * @return Ignore is just a flag to avoid nullable types.
   */
  @CheckResult Observable<Ignore> destroySession();

  /**
   * Check if there is an active session
   *
   * @return true if there is an active session.
   */
  @CheckResult Observable<Boolean> activeSession();

  /**
   * If there is no active session, a RuntimeException is thrown.
   *
   * @return the id-address associated with the current user.
   */
  @CheckResult Observable<String> getAddress();

  class Builder {
    public BsPersonal build(Context context) {
      OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
          .readTimeout(60, TimeUnit.SECONDS)
          .writeTimeout(60, TimeUnit.SECONDS)
          .connectTimeout(60, TimeUnit.SECONDS)
          .build();

      return new BsPersonalRepository(new Persistence(context), new Retrofit.Builder()
          .baseUrl(BsPersonalApi.URL_BASE)
          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
          .addConverterFactory(GsonConverterFactory.create())
          .client(okHttpClient)
          .build().create(BsPersonalApi.class));
    }
  }
}
