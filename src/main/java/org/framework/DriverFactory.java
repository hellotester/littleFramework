package org.framework;

import org.framework.GlobeConfig;
import org.framework.webdriver.Browser;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

import javax.annotation.Nullable;
import java.io.File;

public interface DriverFactory {
    void setupWebDriverBinary();


    MutableCapabilities createCapabilities(GlobeConfig globeConfig, Browser browser,
                                           @Nullable Proxy proxy, @Nullable File browserDownloadsFolder);


    WebDriver create(GlobeConfig globeConfig, Browser browser, @Nullable Proxy proxy, @Nullable File browserDownloadsFolder);
}
