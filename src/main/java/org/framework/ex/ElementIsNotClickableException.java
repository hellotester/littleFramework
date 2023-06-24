package org.framework.ex;


public class ElementIsNotClickableException extends RuntimeException {
    public ElementIsNotClickableException(String elementDescription, Throwable cause) {
        super("Element is not clickable: " + elementDescription, cause);
    }
}
