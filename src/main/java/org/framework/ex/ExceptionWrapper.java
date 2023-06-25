package org.framework.ex;


import org.framework.WebElementFinder;
import org.openqa.selenium.*;


public class ExceptionWrapper {

    public static Throwable wrap(Throwable lastError, WebElementFinder elementFinder) {
        if (lastError instanceof InvalidElementStateException) {
            return new InvalidStateException(elementFinder.alias(), lastError);
        } else if (isElementNotClickableException(lastError)) {
            return new ElementIsNotClickableException(elementFinder.alias(), lastError);
        } else if (lastError instanceof StaleElementReferenceException || lastError instanceof NotFoundException) {
            return new NoSuchElementException(elementFinder.alias(), lastError);
        }
        return lastError;
    }

    public static Throwable wrap(Throwable lastError, String errMsg) {
        if (lastError instanceof InvalidElementStateException) {
            return new InvalidStateException(errMsg, lastError);
        } else if (isElementNotClickableException(lastError)) {
            return new ElementIsNotClickableException(errMsg, lastError);
        } else if (lastError instanceof StaleElementReferenceException || lastError instanceof NotFoundException) {
            return new NoSuchElementException(errMsg, lastError);
        }
        return lastError;
    }


    private static boolean isElementNotClickableException(Throwable e) {
        return e instanceof WebDriverException && e.getMessage().contains("is not clickable");
    }
}
