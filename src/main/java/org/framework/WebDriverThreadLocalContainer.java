package org.framework;

import org.framework.webdriver.WebDriverFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.events.WebDriverListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.currentThread;


public class WebDriverThreadLocalContainer implements DriverContainer {

    private static final Logger log = LoggerFactory.getLogger(WebDriverThreadLocalContainer.class);

    private final List<WebDriverListener> listeners = new ArrayList<>();
    final Collection<Thread> allWebDriverThreads = new ConcurrentLinkedQueue<>();
    final Map<Long, WebDriver> threadWebDriver = new ConcurrentHashMap<>(4);
    private final Map<Long, DownloadsFolder> threadDownloadsFolder = new ConcurrentHashMap<>(4);

    private final GlobeConfig globeConfig = new GlobeConfig();
    private final WebDriverFactory factory = new WebDriverFactory();
    private final WebDriverSweeper webDriverSweeper = new WebDriverSweeper();
    private final DriverGenerator driverGenerator = new DriverGenerator();

    final AtomicBoolean cleanupThreadStarted = new AtomicBoolean(false);


    @Override
    public void addListener(WebDriverListener listener) {
        listeners.add(listener);
    }


    @Override
    public void removeListener(WebDriverListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void setWebDriver(WebDriver webDriver) {
        resetWebDriver();
        threadWebDriver.put(currentThread().getId(), webDriver);
    }


    @Override
    public void resetWebDriver() {
        long threadId = currentThread().getId();
        threadWebDriver.remove(threadId);
        threadDownloadsFolder.remove(threadId);
    }


    @Override
    public boolean hasWebDriverStarted() {
        WebDriver webDriver = threadWebDriver.get(currentThread().getId());
        return webDriver != null;
    }

    @Override
    public Optional<WebDriver> getWebDriver() {
        long threadId = currentThread().getId();
        if (!threadWebDriver.containsKey(threadId)) {
            throw new IllegalStateException("No webdriver is bound to current thread: " + threadId + ". You need to call open(url) first.");
        }
        return Optional.of(threadWebDriver.get(threadId));
    }

    @Override
    @Nonnull
    public WebDriver getAndCheckWebDriver() {
        WebDriver webDriver = threadWebDriver.get(currentThread().getId());
        if (webDriver != null && globeConfig.reopenBrowserOnFail() && !isBrowserStillOpen(webDriver)) {
            log.info("Webdriver has been closed meanwhile. Let's re-create it.");
            closeWebDriver();
            webDriver = createDriver();
        } else if (webDriver == null) {
            log.info("No webdriver is bound to current thread: {} - let's create a new webdriver", currentThread().getId());
            webDriver = createDriver();
        }
        return webDriver;
    }

    @Nonnull
    @Override
    public DownloadsFolder getBrowserDownloadsFolder() {
        return threadDownloadsFolder.get(currentThread().getId());
    }


    @Nonnull
    private WebDriver createDriver() {
        DriverGenerator.Result result = driverGenerator.createDriver(globeConfig, factory, listeners);
        long threadId = currentThread().getId();
        threadWebDriver.put(threadId, result.webDriver);
        if (result.browserDownloadsFolder != null) {
            threadDownloadsFolder.put(threadId, result.browserDownloadsFolder);
        }
        if (globeConfig.holdBrowserOpen()) {
            log.info("Browser will stay open due to holdBrowserOpen=true: {} -> {}", threadId, result.webDriver);
        } else {
            markForAutoClose(currentThread());
        }
        return result.webDriver;
    }


    @Override
    public void closeWindow() {
        getAndCheckWebDriver().close();
    }


    @Override
    public void closeWebDriver() {
        long threadId = currentThread().getId();
        WebDriver driver = threadWebDriver.get(threadId);
        webDriverSweeper.close(globeConfig, driver);
        resetWebDriver();
    }

    @Override
    public void clearBrowserCache() {
        if (hasWebDriverStarted()) {
            getAndCheckWebDriver().manage().deleteAllCookies();
        }
    }

    @Override
    @Nonnull
    public String getPageSource() {
        return getAndCheckWebDriver().getPageSource();
    }

    @Override
    @Nonnull
    public String getCurrentUrl() {
        return getAndCheckWebDriver().getCurrentUrl();
    }

    @Override
    @Nonnull
    public String getCurrentFrameUrl() {
        return ((JavascriptExecutor) getAndCheckWebDriver()).executeScript("return window.location.href").toString();
    }

    @Override
    public GlobeConfig config() {
        return globeConfig;
    }

    private void markForAutoClose(Thread thread) {
        allWebDriverThreads.add(thread);

        if (!cleanupThreadStarted.get()) {
            synchronized (this) {
                if (!cleanupThreadStarted.get()) {
                    new UnusedWebDriversCleanupThread(allWebDriverThreads, threadWebDriver, threadDownloadsFolder).start();
                    cleanupThreadStarted.set(true);
                }
            }
        }
    }


    public boolean isBrowserStillOpen(WebDriver webDriver) {
        try {
            webDriver.getTitle();
            return true;
        } catch (UnreachableBrowserException e) {
            log.debug("Browser is unreachable", e);
            return false;
        } catch (NoSuchWindowException e) {
            log.debug("Browser window is not found", e);
            return false;
        } catch (NoSuchSessionException e) {
            log.debug("Browser session is not found", e);
            return false;
        }
    }
}
