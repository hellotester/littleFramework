package org.framework;

import org.framework.emun.SeleniumConfig;
import org.framework.emun.TestStrategy;
import org.framework.emun.Timeout;

import java.util.List;

public class GlobeConfig {

    private final SeleniumConfig seleniumConfig = SeleniumConfig.getInstance();
    private final Timeout timeout = Timeout.getInstance();

    private final TestStrategy testStrategy = TestStrategy.getInstance();

    public String browser() {
        return seleniumConfig.getBrowser();
    }

    public boolean headless() {
        return seleniumConfig.isHeadless();
    }

    public String remote() {
        return seleniumConfig.getRemoteDriverUrl();
    }

    public String browserSize() {
        return seleniumConfig.getBrowserSize();
    }

    public String browserVersion() {
        return seleniumConfig.getBrowserVersion();
    }


    public String browserPosition() {
        return seleniumConfig.getBrowserPosition();
    }

    public boolean driverManagerEnabled() {
        return seleniumConfig.isDriverManagerEnabled();
    }

    public boolean webDriverLogsEnabled() {
        return seleniumConfig.isWebDriverLogEnabled();
    }

    public String browserBinary() {
        return seleniumConfig.getBrowserBinary();
    }

    public String pageLoadStrategy() {
        return seleniumConfig.getPageLoadStrategy();
    }

    public long pageLoadTimeout() {
        return timeout.getPageLoadTimeout();
    }


    public String baseUrl() {
        return testStrategy.getBaseUrl();
    }


    public long pollingInterval() {
        return timeout.getPollingInterval();
    }

    public boolean holdBrowserOpen() {
        return testStrategy.isHoldBrowserOpen();
    }

    public boolean reopenBrowserOnFail() {
        return testStrategy.isReopenBrowserOnFail();
    }


    public boolean screenshots() {
        return testStrategy.isFailScreenshots();
    }


    public String webDriverLogFolder() {
        return seleniumConfig.getWebDriverLogFolder();
    }

    public String downloadsFolder() {
        return seleniumConfig.getDownloadsFolder();
    }


    public boolean proxyEnabled() {
        return seleniumConfig.isProxyEnabled();
    }

    public String proxyHost() {
        return seleniumConfig.getProxyHost();
    }

    public String proxyPort() {
        return seleniumConfig.getProxyPort();
    }

    public List<String> browserOption() {
        return seleniumConfig.getBrowserOptions();
    }
}
