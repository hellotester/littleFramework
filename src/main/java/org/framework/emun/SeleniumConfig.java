package org.framework.emun;

import org.framework.Value;
import org.framework.util.ClassUtil;

import java.util.*;
import java.util.stream.Collectors;


public enum SeleniumConfig {

    SELENIUM_CONFIG;

    SeleniumConfig() {
        try {
            ClassUtil.injectFieldFromYml(this);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Value("${selenium.browser:chrome}")
    String browser;
    @Value("selenium.browserVersion")
    String browserVersion;
    @Value("selenium.headless")
    boolean headless;
    @Value("selenium.remoteDriverUrl")
    String remoteDriverUrl;
    @Value("selenium.browserSize")
    String browserSize;
    @Value("selenium.browserPosition")
    String browserPosition;
    @Value("${selenium.driverManagerEnabled:true}")
    boolean driverManagerEnabled;
    @Value("selenium.webDriverLogEnabled")
    boolean webDriverLogEnabled;

    @Value("selenium.webDriverLogFolder")
    String webDriverLogFolder;
    @Value("selenium.proxyEnabled")
    boolean proxyEnabled;
    @Value("selenium.proxyHost")
    String proxyHost;
    @Value("selenium.proxyPort")
    String proxyPort;
    @Value("selenium.browserBinary")
    String browserBinary;
    @Value("${selenium.pageLoadStrategy:normal}")
    String pageLoadStrategy;
    @Value("selenium.downloadsFolder")
    String downloadsFolder;
    List<String> browserOptions;

    public List<String> getBrowserOptions() {
        return browserOptions;
    }

    @Value("selenium.browserOptionsArgs")
    void setOptions(String options) {
        if (options != null && !options.isEmpty()) {
            this.browserOptions = Arrays.stream(options.replaceAll("\\[", "")
                            .replaceAll("]", "")
                            .split(","))
                    .collect(Collectors.toList());
        } else {
            this.browserOptions = Collections.emptyList();
        }

    }


    public String getBrowser() {
        return browser;
    }

    public String getBrowserVersion() {
        return browserVersion;
    }

    public boolean isHeadless() {
        return headless;
    }

    public String getRemoteDriverUrl() {
        return remoteDriverUrl;
    }

    public String getBrowserSize() {
        return browserSize;
    }

    public String getBrowserPosition() {
        return browserPosition;
    }

    public boolean isDriverManagerEnabled() {
        return driverManagerEnabled;
    }

    public boolean isWebDriverLogEnabled() {
        return webDriverLogEnabled;
    }

    public boolean isProxyEnabled() {
        return proxyEnabled;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public String getProxyPort() {
        return proxyPort;
    }

    public String getBrowserBinary() {
        return browserBinary;
    }

    public String getPageLoadStrategy() {
        return pageLoadStrategy;
    }

    public String getDownloadsFolder() {
        return downloadsFolder;
    }


    public String getWebDriverLogFolder() {
        return webDriverLogFolder;
    }


    public static SeleniumConfig getInstance() {

        return SELENIUM_CONFIG;
    }


    @Override
    public String toString() {
        return "SeleniumConfig{" +
                "browser='" + browser + '\'' +
                ", browserVersion='" + browserVersion + '\'' +
                ", headless=" + headless +
                ", remoteDriverUrl='" + remoteDriverUrl + '\'' +
                ", browserSize='" + browserSize + '\'' +
                ", browserPosition='" + browserPosition + '\'' +
                ", driverManagerEnabled=" + driverManagerEnabled +
                ", webDriverLogEnabled=" + webDriverLogEnabled +
                ", proxyEnabled=" + proxyEnabled +
                ", proxyHost='" + proxyHost + '\'' +
                ", proxyPort='" + proxyPort + '\'' +
                ", browserBinary='" + browserBinary + '\'' +
                ", pageLoadStrategy='" + pageLoadStrategy + '\'' +
                ", downloadsFolder='" + downloadsFolder + '\'' +
                '}';
    }
}
