package com.tipsi.cameraroll;

/**
 * Created by ngoriachev on 31/08/2018.
 */

public abstract class ArgChecks {

  static void requireNonNull(Object value, String message) {
    require(value != null, message);
  }

  static void require(boolean assetion, String message) {
    if (!assetion) {
      throw new IllegalArgumentException(message);
    }
  }

}
