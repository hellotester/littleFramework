package org.framework;

import org.awaitility.core.ConditionTimeoutException;
import org.framework.emun.Timeout;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;

import static org.awaitility.Awaitility.await;

public class Wait {

    private static final Timeout timeout = Timeout.getInstance();

    public static WebElement waitElementBeSelected(WebElement searchContext) {
        await().pollDelay(timeout.getPollingInterval(), TimeUnit.MILLISECONDS)
                .atMost(timeout.getToVisibleTimeout(), TimeUnit.MILLISECONDS)
                .until(searchContext::isSelected);
        return searchContext;
    }

    public static WebElement waitElementVisible(WebElement searchContext) {
        await().pollDelay(timeout.getPollingInterval(), TimeUnit.MILLISECONDS)
                .atMost(timeout.getToVisibleTimeout(), TimeUnit.MILLISECONDS)
                .until(searchContext::isDisplayed);
        return searchContext;
    }

    public static WebElement waitElementVisible(SearchContext searchContext, By by) {
        return await().ignoreException(NoSuchElementException.class).
                pollDelay(timeout.getPollingInterval(), TimeUnit.MILLISECONDS)
                .atMost(timeout.getSearchDomContentTimeout(), TimeUnit.MILLISECONDS)
                .until(() -> searchContext.findElement(by), WebElement::isDisplayed);
    }

    public static List<WebElement> waitAllElementsVisible(SearchContext searchContext, By by) {
        return await().ignoreException(NoSuchElementException.class)
                .pollDelay(timeout.getPollingInterval(), TimeUnit.MILLISECONDS)
                .atMost(timeout.getSearchDomContentTimeout(), TimeUnit.MILLISECONDS)
                .until(() -> searchContext.findElements(by),
                        es -> !es.isEmpty() && es.stream().allMatch(WebElement::isDisplayed));
    }


    public static WebElement waitElementsVisible(SearchContext searchContext, By by, int index) {
        return await().ignoreException(NoSuchElementException.class)
                .pollDelay(timeout.getPollingInterval(), TimeUnit.MILLISECONDS)
                .atMost(timeout.getSearchDomContentTimeout(), TimeUnit.MILLISECONDS)
                .until(() -> searchContext.findElements(by),
                        es -> !es.isEmpty() && es.size() > index && es.get(index).isDisplayed()).get(index);
    }

    public static WebElement waitElementEnable(WebElement searchContext) {
        await().pollDelay(timeout.getPollingInterval(), TimeUnit.MILLISECONDS)
                .atMost(timeout.getToClickableTimeout(), TimeUnit.MILLISECONDS)
                .until(searchContext::isEnabled);
        return searchContext;
    }


    public static WebElement waitElementClickable(WebElement searchContext) {
        await().pollDelay(timeout.getPollingInterval(), TimeUnit.MILLISECONDS)
                .atMost(timeout.getToClickableTimeout(), TimeUnit.MILLISECONDS)
                .until(() -> searchContext, e -> e.isDisplayed() && e.isEnabled());
        return searchContext;
    }

    public static WebElement waitElementClickable(SearchContext searchContext, By by) {
        return await().ignoreException(NoSuchElementException.class)
                .pollDelay(timeout.getPollingInterval(), TimeUnit.MILLISECONDS)
                .atMost(timeout.getSearchDomContentTimeout(), TimeUnit.MILLISECONDS)
                .until(() -> searchContext.findElement(by), WebElement::isEnabled);
    }

    public static List<WebElement> waitAllElementsClickable(SearchContext searchContext, By by) {
        return await().ignoreException(NoSuchElementException.class)
                .pollDelay(timeout.getPollingInterval(), TimeUnit.MILLISECONDS)
                .atMost(timeout.getSearchDomContentTimeout(), TimeUnit.MILLISECONDS)
                .until(() -> searchContext.findElements(by),
                        es -> !es.isEmpty() && es.stream().allMatch(WebElement::isEnabled));
    }

    public static WebElement waitElementClickable(SearchContext searchContext, By by, int index) {
        return await().ignoreException(NoSuchElementException.class)
                .pollDelay(timeout.getPollingInterval(), TimeUnit.MILLISECONDS)
                .atMost(timeout.getSearchDomContentTimeout(), TimeUnit.MILLISECONDS)
                .until(() -> searchContext.findElements(by),
                        es -> !es.isEmpty() && es.size() > index && es.get(index).isEnabled()).get(index);
    }

    public static WebElement waitElementExist(SearchContext searchContext, By by) {
        return await().ignoreException(NoSuchElementException.class)
                .pollDelay(timeout.getPollingInterval(), TimeUnit.MILLISECONDS)
                .atMost(timeout.getSearchDomContentTimeout(), TimeUnit.MILLISECONDS)
                .until(() -> searchContext.findElement(by), Objects::nonNull);
    }

    public static WebElement waitElementExist(SearchContext searchContext, By by, int index) {
        return await().ignoreException(NoSuchElementException.class)
                .pollDelay(timeout.getPollingInterval(), TimeUnit.MILLISECONDS)
                .atMost(timeout.getSearchDomContentTimeout(), TimeUnit.MILLISECONDS)
                .until(() -> searchContext.findElements(by),
                        es -> !es.isEmpty() && es.size() > index && es.get(index) != null).get(index);
    }


    public static WebElement waitForCssSelectorTobeFound(SearchContext searchContext, String cssSelector) {
        return await().ignoreException(NoSuchElementException.class)
                .pollDelay(timeout.getPollingInterval(), TimeUnit.MILLISECONDS)
                .atMost(timeout.getSearchDomContentTimeout(), TimeUnit.MILLISECONDS)
                .until(() -> searchContext.findElement(By.cssSelector(cssSelector)), Objects::nonNull);
    }

    public static WebElement waitForXpathTobeFound(SearchContext searchContext, String xpath) {
        return await().ignoreException(NoSuchElementException.class)
                .pollDelay(timeout.getPollingInterval(), TimeUnit.MILLISECONDS)
                .atMost(timeout.getSearchDomContentTimeout(), TimeUnit.MILLISECONDS)
                .until(() -> searchContext.findElement(By.xpath(xpath)), Objects::nonNull);
    }

    public static List<WebElement> waitAllElementsExist(SearchContext searchContext, By by) {
        return await().ignoreException(NoSuchElementException.class)
                .pollDelay(timeout.getPollingInterval(), TimeUnit.MILLISECONDS)
                .atMost(timeout.getSearchDomContentTimeout(), TimeUnit.MILLISECONDS)
                .until(() -> searchContext.findElements(by),
                        es -> !es.isEmpty());
    }

    public static void unit(Callable<Boolean> callable) {
        await().pollDelay(timeout.getPollingInterval(), TimeUnit.MILLISECONDS)
                .atMost(timeout.getSearchDomContentTimeout(), TimeUnit.MILLISECONDS)
                .until(callable);
    }

    public static <T> T unit(Callable<T> callable, Predicate<T> matcher) {
        return await().pollDelay(timeout.getPollingInterval(), TimeUnit.MILLISECONDS)
                .atMost(timeout.getSearchDomContentTimeout(), TimeUnit.MILLISECONDS)
                .until(callable, matcher);
    }

    public static void waitPageLoadComplete() {



    }


    private static void throwableRealException(Throwable g, @Nonnull String err_msg) {
        if (g != null) {
            if (g.getCause() != null) {
                throw new RuntimeException(err_msg, g.getCause());
            }
            throw new RuntimeException(err_msg, g);
        }
        throw new RuntimeException(err_msg);
    }


}
