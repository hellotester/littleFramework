package org.framework;

import org.awaitility.core.ConditionTimeoutException;
import org.framework.emun.Timeout;
import org.framework.ui.WrapEleUI;
import org.framework.util.StringUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.WebDriverListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.lang.reflect.*;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;


public final class PageOptional {

    private final static Logger log = LoggerFactory.getLogger(PageOptional.class);

    private PageOptional() {
    }

    private final static DriverContainer driverContainer = new WebDriverThreadLocalContainer();


    public static DriverContainer getDriverContainer() {
        return driverContainer;
    }

    public static void addListener(WebDriverListener listener) {
        driverContainer.addListener(listener);
    }

    public static void removeListener(WebDriverListener listener) {
        driverContainer.removeListener(listener);
    }

    public static void setWebDriver(WebDriver webDriver) {
        driverContainer.setWebDriver(webDriver);
    }

    public static Optional<WebDriver> driver() {
        return driverContainer.getWebDriver();
    }

    @Nonnull
    public static WebDriver getAndCheckWebDriver() {
        return driverContainer.getAndCheckWebDriver();
    }

    public static void open() {
        getAndCheckWebDriver();
    }

    public static void open(@Nonnull String url) {
        String absoluteUrl = url;
        if (!isAbsoluteUrl(url)) {
            String baseUrl = driverContainer.config().baseUrl();
            if (StringUtil.isNotBlank(baseUrl)) {
                url = StringUtil.removeBeforeAndAfterCharacter(url, "/", null);
                baseUrl = StringUtil.removeBeforeAndAfterCharacter(baseUrl, null, "/");
                absoluteUrl = baseUrl + "/" + url;
            } else {
                throw new RuntimeException("MalformedURLException." + url);
            }
        }
        getAndCheckWebDriver().get(absoluteUrl);
    }


    static boolean isAbsoluteUrl(String url) {
        try {
            new URL(url);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * create page object and open window,
     * but if the page object has not url method or url field will throw RuntimeException
     *
     * @param pageClass
     * @param <Page>
     * @return pageObj
     */
    public static <Page> Page visit(Class<Page> pageClass) {
        return visit(pageClass, null);
    }

    /**
     * create page object and open window to url,
     *
     * @param pageClass
     * @param url
     * @param <Page>
     * @return pageObject
     */
    public static <Page> Page visit(Class<Page> pageClass, String url) {
        try {
            Page page = PageFactory.loadPage(pageClass, getAndCheckWebDriver());
            if (StringUtil.isBlank(url)) {
                url = getUrlFromCurrentPageObject(page);
                if (StringUtil.isBlank(url)) {
                    throw new RuntimeException("not found current page url");
                }
            }
            open(url);
            return page;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("page init fail.", e);
        }
    }

    /**
     * Do not use the create method until you start the browser.
     * You should use the visit method and specify the url parameter
     *
     * @param pageClass
     * @param <Page>
     * @return pageObject
     */
    public static <Page> Page create(Class<Page> pageClass) {
        // if browser has not started
        if (!driverContainer.hasWebDriverStarted()) {
            log.warn("browser is not started.... will execute visit method.");
            visit(pageClass);
        }
        try {
            return PageFactory.loadPage(pageClass, driver().get());
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * i don't want to write.
     *
     * @param locator
     */
    public static void waitForClick(String locator) {
        get(locator).waitForClick();
    }


    public static void waitForRightClick(String locator) {
        //run(locator, "rightClick");
        get(locator).waitForRightClick();
    }


    public static void waitForDoubleClick(String locator) {
        //run(locator, "doubleClick");
        get(locator).waitForDoubleClick();
    }


    public static void waitForJSClick(String locator) {
        // run(locator, "JsClick");
        get(locator).waitForJSClick();
    }


    public static void waitForType(String locator, String text) {
        //run(locator, "type", text);
        get(locator).waitForType(text);
    }


    public static void waitForClickAndType(String locator, String text) {
        //run(locator, new String[]{"click", "type"}, text);
        get(locator).waitForClickAndType(text);
    }


    public static void waitForSetValue(String locator, String text) {
        waitForSetAttribute(locator, "value", text);
    }

    public static void waitForSetInnerText(String locator, String text) {
        waitForSetAttribute(locator, "innerText", text);
    }

    public static void waitForSetAttribute(String locator, String attName, String attVal) {
        //run(locator, "setAttribute", attName, attVal);
        get(locator).setAttribute(attName, attVal);
    }

    public static String getElementAttributeForValue(String locator, String attName) {
        return get(locator).getAttribute(attName);
        //return (String) run(locator, "getAttribute", attName);
    }


    public static String getElementCssValue(String locator, String cssAttributeName) {
        return get(locator).getCssValue(cssAttributeName);
        //return (String) run(locator, "getCssAttribute", cssAttributeName);
    }


    public static void waitForSelect(String locator, String optionText) {
        //run(locator, "select", optionText);
        get(locator).waitForSelect(optionText);

    }

    public static void waitForClickSelect(String locator, String optionText) {
        //run(locator, new String[]{"click", "select"}, optionText);
        get(locator).waitForClickSelect(optionText);
    }

    public static void waitForSelect(String locator, int optionIndex) {
        //run(locator, "select", optionIndex);
        get(locator).waitForSelect(optionIndex);
    }

    public static void waitForClickSelect(String locator, int optionIndex) {
        //run(locator, new String[]{"click", "select"}, optionIndex);
        get(locator).waitForClickSelect(optionIndex);
    }

    public static void waitForDropTo(String fromLocator, String toLocator) {
        get(fromLocator).dropTo(toLocator);
    }


    public static WrapEleUI get(String locator) {
        if (driverContainer.hasWebDriverStarted()) {
            return (WrapEleUI) Proxy.newProxyInstance(PageOptional.class.getClassLoader(),
                    new Class[]{WrapEleUI.class},
                    new ElementProxy(WebElementFinder.with(getAndCheckWebDriver(), locator)));
        }
        throw new RuntimeException("browser not started.");
    }

    public static WebElement selenium(String locator){
        checkHasBrowser();
        return WebElementFinder.with(getAndCheckWebDriver(), locator).findElement();
    }


    public static void highlightActiveElement(String locator) {
        waitForSetAttribute(locator, "style", "background: red; border: 1px solid black");
    }

    /**
     * @return temp file .png
     */
    public static File getSnapshotForActiveElement() {
        if (!driverContainer.hasWebDriverStarted()) {
            throw new RuntimeException("browser not started.");
        }
        return getAndCheckWebDriver().switchTo().activeElement().getScreenshotAs(OutputType.FILE);
    }

    /**
     * temp file
     *
     * @return
     */
    public static File getSnapshotForScreen() {
        if (!driverContainer.hasWebDriverStarted()) {
            throw new RuntimeException("browser not started.");
        }
        return ((RemoteWebDriver) getAndCheckWebDriver()).getScreenshotAs(OutputType.FILE);
    }

    public static String getTitle() {
        if (driverContainer.hasWebDriverStarted()) {
            return getAndCheckWebDriver().getTitle();
        }
        throw new RuntimeException("browser not started.");
    }

    public static Set<String> getAllTabs() {
        checkHasBrowser();
        Set<String> tabs = new LinkedHashSet<>();
        WebDriver driver = getAndCheckWebDriver();
        String origin = driver.getWindowHandle();
        for (String windowHandle : driver.getWindowHandles()) {
            tabs.add(driver.switchTo().window(windowHandle).getTitle());
        }
        driver.switchTo().window(origin);
        return tabs;
    }

    public static void iframe(String locator){
        checkHasBrowser();
        getAndCheckWebDriver().switchTo().frame(selenium(locator));
    }


    public static void switchWindowByTitle(String title) {
        checkHasBrowser();
        boolean isTarget = false;
        for (String w : getAndCheckWebDriver().getWindowHandles()) {
            String tit = getAndCheckWebDriver().switchTo().window(w).getTitle();
            if (StringUtil.isEquals(tit, title)) {
                isTarget = true;
                break;
            }
        }
        if (!isTarget) {
            log.debug("has tabs :{}", getAllTabs());
            throw new NotFoundException("can't find title be " + title);
        }
    }

    public static void shouldBeNumberOfWindows(int num) {
        try {
            WebDriver driver = getAndCheckWebDriver();
            Wait.unit(() -> num == driver.getWindowHandles().size());
        } catch (ConditionTimeoutException e) {
            throw new AssertionError(String.format("%s tabs were expected, but there were only %s tabs", num,
                    getAndCheckWebDriver().getWindowHandles().size()));
        }
    }


    public static void shouldBeTitle(String title) {
        try {
            WebDriver driver = getAndCheckWebDriver();
            Wait.unit(() -> StringUtil.isEquals(driver.getTitle(), title));
        } catch (ConditionTimeoutException e) {
            throw new AssertionError(String.format("the current page title is expected to be '%s'  but is actually '%s'", title,
                    getTitle()));
        }
    }

    public static void shouldContentText(String text) {
        try {
            WebDriver driver = getAndCheckWebDriver();
            Wait.unit(() -> driver.getPageSource().contains(text));
        } catch (ConditionTimeoutException e) {
            throw new AssertionError(String.format("the current page have tex is expected to be '%s'  but is actually '%s'", text,
                    getTitle()));
        }
    }

    public static void checkHasBrowser() {
        if (!driverContainer.hasWebDriverStarted()) {
            throw new RuntimeException("browser not started.");
        }
    }


    // https://blog.csdn.net/qq_50854790/article/details/123610184
    public static WrapEleUI findAncestor(String locator, String parent) {
        return get(locator).ancestor(parent);
    }


    public static WrapEleUI findDescendant(String locator, String child) {
        return get(locator).descendant(child);
    }

    public static WrapEleUI findSibling(String locator, int sibling) {
        return get(locator).sibling(sibling);
    }


    public static void waitForAjax() {
        checkHasBrowser();
        WebDriver driver = getAndCheckWebDriver();
        Wait.unit(() -> Boolean.TRUE.equals(JavaScript.waitForAjax.execute(driver)), Timeout.getInstance().getWaitForAjaxTimeout());
    }


    public static void waitForAjaxRequest(String requestPartialUrl, int timeoutInSeconds) {
        checkHasBrowser();
        WebDriver driver = getAndCheckWebDriver();
        Wait.unit(() -> Boolean.TRUE.equals(JavaScript.waitForAjaxUrl.execute(driver, requestPartialUrl)), timeoutInSeconds);
    }


    public static void waitPageLoadsCompletely() {
        checkHasBrowser();
        WebDriver andCheckWebDriver = getAndCheckWebDriver();
        Wait.unit(() -> ((JavascriptExecutor) andCheckWebDriver)
                        .executeScript("return document.readyState").equals("complete"),
                Timeout.getInstance().getPageLoadTimeout());
        waitForAjax();
    }


    private static Object run(String locator, String commands) throws Exception {
        return run(locator, new String[]{commands});
    }

    private static Object run(String locator, String commands, Object... args) throws Exception {
        return run(locator, new String[]{commands}, args);
    }


    private static Object run(String locator, String[] commands, Object... args) throws Exception {
        if (!driverContainer.hasWebDriverStarted()) {
            throw new RuntimeException("browser not started.");
        }
        Object result = null;
        for (String command : commands) {
            result = CommandRunner.getInstance().execute(null, WebElementFinder.with(getAndCheckWebDriver(), locator), command, args);
        }
        return result;
    }

    private static String getUrlFromCurrentPageObject(Object pageObj) {
        AccessibleObject acc;
        try {
            acc = pageObj.getClass().getMethod("url");
        } catch (NoSuchMethodException e) {
            try {
                acc = pageObj.getClass().getDeclaredField("url");
            } catch (NoSuchFieldException fieldException) {
                // 既没有定义名为url的方法 也没有 url字段
                return null;
            }
        }
        acc.setAccessible(true);
        if (acc instanceof Method) {
            try {
                return (String) ((Method) acc).invoke(pageObj);
            } catch (InvocationTargetException | IllegalAccessException e) {
                return null;
            }
        }
        try {
            return (String) ((Field) acc).get(pageObj);
        } catch (IllegalAccessException e) {
            return null;
        }

    }
}
