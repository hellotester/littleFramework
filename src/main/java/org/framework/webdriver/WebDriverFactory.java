package org.framework.webdriver;

import org.framework.DriverFactory;
import org.framework.GlobeConfig;
import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.openqa.selenium.remote.Browser.*;


public class WebDriverFactory {
    private static final Logger log = LoggerFactory.getLogger(WebDriverFactory.class);

    private final Map<String, Class<? extends AbstractDriverFactory>> factories = factories();
    private final RemoteDriverFactory remoteDriverFactory = new RemoteDriverFactory();


    @Nonnull
    private Map<String, Class<? extends AbstractDriverFactory>> factories() {
        Map<String, Class<? extends AbstractDriverFactory>> result = new HashMap<>();
        result.put(CHROME.browserName(), ChromeDriverFactory.class);
        result.put(FIREFOX.browserName(), FirefoxDriverFactory.class);
        result.put(EDGE.browserName(), EdgeDriverFactory.class);
        result.put(IE.browserName(), InternetExplorerDriverFactory.class);
        result.put("edge", EdgeDriverFactory.class);
        result.put("ie", InternetExplorerDriverFactory.class);
        return result;
    }


    @Nonnull
    public WebDriver createWebDriver(GlobeConfig globeConfig, @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
        log.debug("browser={}", globeConfig.browser());
        log.debug("browser.version={}", globeConfig.browserVersion());
        log.debug("remote={}", globeConfig.remote());
        log.debug("browserSize={}", globeConfig.browserSize());
        if (browserDownloadsFolder != null) {
            log.debug("downloadsFolder={}", browserDownloadsFolder.getAbsolutePath());
        }

        Browser browser = new Browser(globeConfig.browser(), globeConfig.headless());
        WebDriver webdriver = createWebDriverInstance(globeConfig, browser, proxy, browserDownloadsFolder);

        adjustBrowserSize(globeConfig, webdriver);
        adjustBrowserPosition(globeConfig, webdriver);
        setLoadTimeout(globeConfig, webdriver);

        logBrowserVersion(webdriver);
        logSeleniumInfo();
        return webdriver;
    }

    private void setLoadTimeout(GlobeConfig globeConfig, WebDriver webdriver) {
        try {
            webdriver.manage().timeouts().pageLoadTimeout(Duration.ofMillis(globeConfig.pageLoadTimeout()));
        } catch (UnsupportedCommandException e) {
            log.info("Failed to set page load timeout to {} ms: {}", globeConfig.pageLoadTimeout(), e.toString());
        } catch (RuntimeException e) {
            log.error("Failed to set page load timeout to {} ms", globeConfig.pageLoadTimeout(), e);
        }
    }


    @Nonnull
    private WebDriver createWebDriverInstance(GlobeConfig globeConfig, Browser browser,
                                              @Nullable Proxy proxy,
                                              @Nullable File browserDownloadsFolder) {
        DriverFactory factory = findFactory(browser);

        if (globeConfig.remote() != null && !globeConfig.remote().isEmpty()) {
            MutableCapabilities capabilities = factory.createCapabilities(globeConfig, browser, proxy, browserDownloadsFolder);
            return remoteDriverFactory.create(globeConfig, capabilities);
        } else {
            if (globeConfig.driverManagerEnabled()) {
                factory.setupWebDriverBinary();
            }

            return factory.create(globeConfig, browser, proxy, browserDownloadsFolder);
        }
    }


    @Nonnull
    private DriverFactory findFactory(Browser browser) {
        Class<? extends AbstractDriverFactory> factoryClass = factories.getOrDefault(
                browser.name.toLowerCase(), ChromeDriverFactory.class);
        try {
            return factoryClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException("Failed to initialize " + factoryClass.getName(), e);
        }
    }

    private void logSeleniumInfo() {
        BuildInfo seleniumInfo = new BuildInfo();
        log.info("Selenium WebDriver v. {} build revision: {}", seleniumInfo.getReleaseLabel(), seleniumInfo.getBuildRevision());
    }

    private void logBrowserVersion(WebDriver webdriver) {
        if (webdriver instanceof HasCapabilities) {
            Capabilities capabilities = ((HasCapabilities) webdriver).getCapabilities();
            log.info("BrowserName={} Version={} Platform={}",
                    capabilities.getBrowserName(), capabilities.getBrowserVersion(), capabilities.getPlatformName());
        } else {
            log.info("BrowserName={}", webdriver.getClass().getName());
        }
    }

    void adjustBrowserPosition(GlobeConfig globeConfig, WebDriver driver) {
        if (globeConfig.browserPosition() != null && !globeConfig.browserPosition().isEmpty()) {
            log.info("Set browser position to {}", globeConfig.browserPosition());
            String[] coordinates = globeConfig.browserPosition().split("x");
            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);
            Point target = new Point(x, y);
            Point current = driver.manage().window().getPosition();
            if (!current.equals(target)) {
                driver.manage().window().setPosition(target);
            }
        }
    }

    void adjustBrowserSize(GlobeConfig globeConfig, WebDriver driver) {
        if (globeConfig.browserSize() != null && !globeConfig.browserSize().isEmpty()) {
            log.info("Set browser size to {}", globeConfig.browserSize());
            String[] dimension = globeConfig.browserSize().split("x");
            int width = Integer.parseInt(dimension[0]);
            int height = Integer.parseInt(dimension[1]);
            driver.manage().window().setSize(new org.openqa.selenium.Dimension(width, height));
        }
    }
}
