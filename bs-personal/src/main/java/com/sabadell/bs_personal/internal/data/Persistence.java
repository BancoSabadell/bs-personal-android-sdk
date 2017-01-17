package com.sabadell.bs_personal.internal.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

/**
 * A simple caching layer to store the user data using shared preferences.
 */
public class Persistence {
  private final SharedPreferences preferences;
  private final String KEY_ADDRESS = "key_address";

  public Persistence(Context context) {
    this.preferences =
        context.getSharedPreferences(BsPersonalRepository.class.getName(), Context.MODE_PRIVATE);
  }

  void saveAddress(String address) {
    SharedPreferences.Editor editor = preferences.edit();
    editor.putString(KEY_ADDRESS, address);
    editor.apply();
  }

  @Nullable String getAddress() {
    return preferences.getString(KEY_ADDRESS, null);
  }

  void removeAccount() {
    SharedPreferences.Editor editor = preferences.edit();
    editor.remove(KEY_ADDRESS);
    editor.apply();
  }
}
