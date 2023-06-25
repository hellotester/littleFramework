package org.framework.ex;

public class ElementNoDisplayedException extends RuntimeException {
    public ElementNoDisplayedException(String elementDescription, Throwable cause) {
        super("Element is not visible: " + elementDescription, cause);
    }

}
