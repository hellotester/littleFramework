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

    @Nonnull
    public By selocator() {
        return selocator;
    }

    @Nonnull
    private final By selocator;
    private final String locator;

    public String getAlias() {
        return Objects.isNull(alias) ? locator : alias;
    }

    private final String alias;
    private final int index;

    public WebDriver driver() {
        return webDriver;
    }


    private WebElementFinder(@Nonnull WebDriver webDriver, WebElementFinder finder, @Nonnull By selocator, int index, String alias) {
        this.webDriver = webDriver;
        this.parentFinder = finder;
        this.selocator = selocator;
        this.locator = selocator.toString().replaceAll("By.", "");
        this.alias = alias;
        this.index = index;
    }


    public static WebElementFinder with(WebDriver driver, String locator) {
        return with(driver, locatorExpressionConvertToBy(driver, locator));
    }

    public static WebElementFinder with(WebDriver driver, By by) {
        return with(driver, null, by, 0, null);
    }

    public static WebElementFinder with(WebDriver webDriver, String locator, String alias) {
        return with(webDriver, null, locatorExpressionConvertToBy(webDriver, locator), initIndexFromLocatorExpression(locator), alias);
    }

    public static WebElementFinder with(WebDriver driver, WebElementFinder parent, By by, int index, String alias) {
        return new WebElementFinder(driver, parent, by, index, alias);
    }


    public WebElement findElement() {
        log.info("from page {} search alias:{} selocator: {} index: {}", driver().getTitle(), alias, locator, index);
        return Objects.equals(index, 0) ?
                Objects.nonNull(parentFinder) ? Wait.waitElementExist(parentFinder.findElement(), selocator)
                        : Wait.waitElementExist(webDriver, selocator) :
                Objects.nonNull(parentFinder) ? Wait.waitElementExist(parentFinder.findElement(), selocator, index) :
                        Wait.waitElementExist(webDriver, selocator, index);
    }


    static Pattern indexRule = Pattern.compile(".*\\{(?<index>\\d)}$");

    private static By locatorExpressionConvertToBy(WebDriver driver, String locator) {
        By by;
        if (StringUtil.isBlank(locator)) {
            throw new IllegalArgumentException("locator expression can't be empty!");
        }
        int index = initIndexFromLocatorExpression(locator);
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


    private static int initIndexFromLocatorExpression(String locator) {
        Matcher matcher = indexRule.matcher(locator);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group("index"));
        }
        return 0;
    }

}
