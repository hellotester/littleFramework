package org.framework.ex;


import org.framework.WebElementFinder;
import org.openqa.selenium.*;


public class ExceptionWrapper {

    public Throwable wrap(Throwable lastError, WebElementFinder elementFinder) {
        if (lastError instanceof InvalidElementStateException) {
            return new InvalidStateException(elementFinder.getAlias(), lastError);
        } else if (isElementNotClickableException(lastError)) {
            return new ElementIsNotClickableException(elementFinder.getAlias(), lastError);
        } else if (lastError instanceof StaleElementReferenceException || lastError instanceof NotFoundException) {
            return new NoSuchElementException(elementFinder.getAlias(), lastError);
        }
        return lastError;
    }


    private boolean isElementNotClickableException(Throwable e) {
        return e instanceof WebDriverException && e.getMessage().contains("is not clickable");
    }
}
