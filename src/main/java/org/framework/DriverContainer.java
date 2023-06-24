package org.framework;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.WebDriverListener;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface DriverContainer {


    void addListener(WebDriverListener listener);

    void removeListener(WebDriverListener listener);

    void setWebDriver(WebDriver webDriver);


    void resetWebDriver();


    Optional<WebDriver> getWebDriver();


    @Nonnull
    WebDriver getAndCheckWebDriver();

    @Nonnull
    DownloadsFolder getBrowserDownloadsFolder();

    void closeWindow();

    void closeWebDriver();

    boolean hasWebDriverStarted();

    void clearBrowserCache();

    @Nonnull
    String getPageSource();

    @Nonnull
    String getCurrentUrl();

    @Nonnull
    String getCurrentFrameUrl();

    GlobeConfig config();
}


