package org.framework;

import org.apache.commons.lang3.StringUtils;
import org.framework.webdriver.WebDriverFactory;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.net.HostIdentifier;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.List;

import static java.lang.Thread.currentThread;
import static org.framework.util.FileUtil.ensureFolderExists;

public class DriverGenerator {
    private static final Logger log = LoggerFactory.getLogger(DriverGenerator.class);

    public DriverGenerator() {
    }

    @Nonnull
    public Result createDriver(GlobeConfig globeConfig,
                               WebDriverFactory factory,
                               List<WebDriverListener> listeners) {
        if (!globeConfig.reopenBrowserOnFail()) {
            throw new IllegalStateException("No webdriver is bound to current thread: " + currentThread().getId() +
                    ", and cannot create a new webdriver because reopenBrowserOnFail=false");
        }

        log.debug("Creating webdriver in thread {} (ip: {}, host: {})...",
                currentThread().getId(), HostIdentifier.getHostAddress(), HostIdentifier.getHostName());

        Proxy seleniumProxy = null;
        if (globeConfig.proxyEnabled()) {
            if (StringUtils.isNoneBlank(globeConfig.proxyHost()) && StringUtils.isNoneBlank(globeConfig.proxyPort())) {
                seleniumProxy = createSeleniumProxy(globeConfig.proxyHost(), Integer.parseInt(globeConfig.proxyPort()));
                log.info("use proxy host:{}", seleniumProxy.getHttpProxy());
            } else {
                log.warn("The proxy mode is expected to start but the remote proxy host address is not configured.");
            }
        }

        File browserDownloadsFolder = globeConfig.remote() != null ? null :
                ensureFolderExists(new File(globeConfig.downloadsFolder()).getAbsoluteFile());

        WebDriver webdriver = factory.createWebDriver(globeConfig, seleniumProxy, browserDownloadsFolder);

        log.info("Created webdriver in thread {}: {} -> {}",
                currentThread().getId(), webdriver.getClass().getSimpleName(), webdriver);

        WebDriver webDriver = addListeners(webdriver, listeners);
        DownloadsFolder downloadsFolder = new DownloadsFolder(browserDownloadsFolder);
        Runtime.getRuntime().addShutdownHook(new ThreadShutdownHook(globeConfig, webDriver, downloadsFolder));

        return new Result(webDriver, downloadsFolder);
    }

    @Nonnull
    private WebDriver addListeners(WebDriver webdriver,
                                   List<WebDriverListener> listeners) {
        return addWebDriverListeners(webdriver, listeners);
    }


    @Nonnull
    private WebDriver addWebDriverListeners(WebDriver webdriver, List<WebDriverListener> listeners) {
        if (listeners.isEmpty()) {
            return webdriver;
        }

        log.info("Add listeners to webdriver: {}", listeners);
        EventFiringDecorator wrapper = new EventFiringDecorator(listeners.toArray(new WebDriverListener[]{}));
        return wrapper.decorate(webdriver);
    }

    public static Proxy createSeleniumProxy(String host, int port) {
        Proxy proxy = new Proxy();
        proxy.setProxyType(Proxy.ProxyType.MANUAL);
        String proxyStr = String.format("%s:%d", host, port);
        proxy.setHttpProxy(proxyStr);
        proxy.setSslProxy(proxyStr);
        return proxy;
    }


    public static class Result {
        public final WebDriver webDriver;

        @Nullable
        public final DownloadsFolder browserDownloadsFolder;

        public Result(WebDriver webDriver, @Nullable DownloadsFolder browserDownloadsFolder) {
            this.webDriver = webDriver;
            this.browserDownloadsFolder = browserDownloadsFolder;
        }
    }
}
