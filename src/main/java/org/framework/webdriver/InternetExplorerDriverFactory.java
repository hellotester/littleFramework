package org.framework.webdriver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.framework.GlobeConfig;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;


public class InternetExplorerDriverFactory extends AbstractDriverFactory {
  private static final Logger log = LoggerFactory.getLogger(InternetExplorerDriverFactory.class);

  @Override
  public void setupWebDriverBinary() {
    if (isSystemPropertyNotSet("webdriver.ie.driver")) {
      WebDriverManager.iedriver().setup();
    }
  }

  @Override
  @Nonnull
  public WebDriver create(GlobeConfig globeConfig, Browser browser, @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    InternetExplorerOptions options = createCapabilities(globeConfig, browser, proxy, browserDownloadsFolder);
    return new InternetExplorerDriver(options);
  }

  @Override
  @Nonnull
  public InternetExplorerOptions createCapabilities(GlobeConfig globeConfig, Browser browser,
                                                    @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    Capabilities capabilities = createCommonCapabilities(new InternetExplorerOptions(), globeConfig, browser, proxy);
    InternetExplorerOptions options = new InternetExplorerOptions(capabilities);
    if (!globeConfig.browserBinary().isEmpty()) {
      log.info("Using browser binary: {}", globeConfig.browserBinary());
      log.warn("Changing browser binary not supported in InternetExplorer, setting will be ignored.");
    }
    return options;
  }
}
