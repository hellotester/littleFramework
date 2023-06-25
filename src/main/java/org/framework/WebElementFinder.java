package org.framework;

import org.framework.util.StringUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class WebElementFinder {

    static Logger log = LoggerFactory.getLogger(WebElementFinder.class);

    @Nonnull
    private final WebDriver webDriver;

    private final WebElementFinder parentFinder;


    public By selocator() {
        return selocator;
    }


    private By selocator;
    private String originlocator;

    public String alias() {
        if (Objects.isNull(alias)) {
            if (StringUtil.isNotBlank(originlocator)) {
                return originlocator + " index:" + index;
            }
            return selocator.toString() + "index:" + index;
        }
        return alias;
    }

    private final String alias;
    private int index;

    public WebDriver driver() {
        return webDriver;
    }


    private WebElementFinder(@Nonnull WebDriver webDriver, WebElementFinder finder, By selocator, String originlocator, int index, String alias) {
        this.webDriver = webDriver;
        this.parentFinder = finder;
        this.selocator = selocator;
        this.originlocator = originlocator;
        this.alias = alias;
        this.index = index;
    }


    public static WebElementFinder with(WebDriver driver, String locator) {
        return with(driver, locator, null);
    }

    public static WebElementFinder with(WebDriver driver, By by) {
        return with(driver, null, by, null, 0, null);
    }

    public static WebElementFinder with(WebDriver webDriver, String locator, String alias) {
        return with(webDriver, null, null, locator, 0, alias);
    }

    public static WebElementFinder with(WebDriver driver, WebElementFinder parent, By by, String locator, int index, String alias) {
        return new WebElementFinder(driver, parent, by, locator, index, alias);
    }


    public WebElement findElement() {
        if (selocator == null && originlocator == null) {
            throw new IllegalArgumentException("no locator!");
        }
        if (selocator == null) {
            selocator = locatorExpressionConvertToBy(webDriver, originlocator);
        }
        if (index == 0 && originlocator != null) {
            index = getIndexFromLocatorExpression(originlocator);
        }
        log.info("from page {} search alias:{} selocator: {} index: {}", driver().getTitle(), alias, selocator, index);
        if (parentFinder != null) {
            return Wait.waitElementExist(parentFinder.findElement(), selocator, index);
        } else {
            return Wait.waitElementExist(webDriver, selocator, index);
        }

    }


    static Pattern indexRule = Pattern.compile(".*\\{(?<index>\\d)}$");

    private static By locatorExpressionConvertToBy(WebDriver driver, String locator) {
        By by;
        if (StringUtil.isBlank(locator)) {
            throw new IllegalArgumentException("locator expression can't be empty!");
        }
        int index = getIndexFromLocatorExpression(locator);
        if (index != 0) {
            locator = locator.replaceAll("\\{" + index + "}", "");
        }
        if (locator.matches("[\\u4e00-\\u9fa5]+")) {
            String test_id = (String) JavaScript.findElementByStr.execute(driver, locator);
            if (StringUtil.isBlank(test_id)) {
                throw new NoSuchElementException("not found element contains text " + locator);
            }
            locator = String.format("[test-id='%s']", test_id);
        }
        if (locator.startsWith("/")) {
            by = By.xpath(locator);
        } else {
            by = By.cssSelector(locator);
        }
        return by;
    }


    private static int getIndexFromLocatorExpression(String locator) {
        Matcher matcher = indexRule.matcher(locator);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group("index"));
        }
        return 0;
    }

}
