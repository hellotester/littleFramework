package org.framework.ex;

import org.framework.Cleanup;


public class InvalidStateException extends RuntimeException {
  public InvalidStateException(String elementDescription, Throwable cause) {
    super("Invalid element state [" + elementDescription + "]: " +
      Cleanup.of.webdriverExceptionMessage(cause.getMessage()), cause);
  }

  public InvalidStateException(String elementDescription, String message) {
    super("Invalid element state [" + elementDescription + "]: " + message);
  }
}
