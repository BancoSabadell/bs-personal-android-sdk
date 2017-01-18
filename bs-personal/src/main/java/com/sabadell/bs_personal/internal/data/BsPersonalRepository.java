package com.sabadell.bs_personal.internal.data;

import com.sabadell.bs_personal.BsPersonal;
import com.sabadell.bs_personal.Ignore;
import com.sabadell.bs_personal.internal.net.BsPersonalApi;
import com.sabadell.bs_personal.internal.net.NetworkResponse;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

public final class BsPersonalRepository implements BsPersonal {
  private final BsPersonalApi api;
  private final NetworkResponse networkResponse;
  private final Persistence persistence;
  static final String NOT_ACTIVE_SESSION =
      "There is no active session. Call createAccount() to create one.",
      ACTIVE_SESSION =
          "There is already an active session. Call destroySession() before attempting to create a new one.",
      PASSWORD_VALIDATION = "Invalid password",
      ADDRESS_VALIDATION = "Invalid address";

  public BsPersonalRepository(Persistence persistence, BsPersonalApi api) {
    this.persistence = persistence;
    this.api = api;
    this.networkResponse = new NetworkResponse();
  }

  @Override public Observable<String> createAccount(String password) {
    if (password == null || password.isEmpty()) {
      return Observable.error(new RuntimeException(PASSWORD_VALIDATION));
    }

    String address = persistence.getAddress();
    if (address != null) return Observable.error(new RuntimeException(ACTIVE_SESSION));

    return api.createSession(password)
        .compose(networkResponse.<Address>process())
        .map(new Function<Address, String>() {
          @Override public String apply(Address address) throws Exception {
            persistence.saveAddress(address.getValue());
            return address.getValue();
          }
        });
  }

  @Override public Observable<Ignore> login(String address) {
    if (address == null || address.isEmpty()) {
      return Observable.error(new RuntimeException(ADDRESS_VALIDATION));
    }

    if (persistence.getAddress() != null) {
      return Observable.error(new RuntimeException(ACTIVE_SESSION));
    }

    persistence.saveAddress(address);
    return Observable.just(Ignore.Instance);
  }

  @Override public Observable<Boolean> activeSession() {
    return Observable.just(persistence.getAddress() != null);
  }

  @Override public Observable<String> getAddress() {
    String address = persistence.getAddress();
    if (address == null) return Observable.error(new RuntimeException(NOT_ACTIVE_SESSION));
    return Observable.just(persistence.getAddress());
  }

  @Override public Observable<Ignore> destroySession() {
    String address = persistence.getAddress();
    if (address == null) return Observable.error(new RuntimeException(NOT_ACTIVE_SESSION));
    persistence.removeAccount();
    return Observable.just(Ignore.Instance);
  }
}
