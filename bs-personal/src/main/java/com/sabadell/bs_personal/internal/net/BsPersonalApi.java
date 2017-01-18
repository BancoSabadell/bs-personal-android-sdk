package com.sabadell.bs_personal.internal.net;

import com.sabadell.bs_personal.internal.data.Address;
import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface BsPersonalApi {
  String URL_BASE = "http://localhost:8080/personal/api/v1/";

  @FormUrlEncoded
  @POST("account") Observable<Response<Address>> createSession(@Field("password") String password);
}
