package org.framework.webdriver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.framework.GlobeConfig;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;
import java.util.Map;


public class ChromeDriverFactory extends AbstractChromiumDriverFactory {
  private static final Logger log = LoggerFactory.getLogger(ChromeDriverFactory.class);


  @Override
  public void setupWebDriverBinary() {
    if (isSystemPropertyNotSet("webdriver.chrome.driver")) {
      WebDriverManager.chromedriver().setup();
    }
  }




  @SuppressWarnings("deprecation")
  public WebDriver create(GlobeConfig globeConfig, Browser browser, @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    MutableCapabilities chromeOptions = createCapabilities(globeConfig, browser, proxy, browserDownloadsFolder);
    log.debug("Chrome options: {}", chromeOptions);
    return new ChromeDriver(buildService(globeConfig), chromeOptions);
  }



  protected ChromeDriverService buildService(GlobeConfig globeConfig) {
    return withLog(globeConfig, new ChromeDriverService.Builder());
  }




  public MutableCapabilities createCapabilities(GlobeConfig globeConfig, Browser browser,
                                                @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    ChromeOptions commonCapabilities = createCommonCapabilities(new ChromeOptions(), globeConfig, browser, proxy);

    ChromeOptions options = new ChromeOptions();
    options.setHeadless(globeConfig.headless());
    if (!globeConfig.browserBinary().isEmpty()) {
      log.info("Using browser binary: {}", globeConfig.browserBinary());
      options.setBinary(globeConfig.browserBinary());
    }
    options.addArguments(createChromeArguments(globeConfig, browser));
    options.setExperimentalOption("excludeSwitches", excludeSwitches(commonCapabilities));
    options.setExperimentalOption("prefs", prefs(browserDownloadsFolder, System.getProperty("chromeoptions.prefs", "")));
    setMobileEmulation(options);
    return options.merge(commonCapabilities);
  }



  protected List<String> createChromeArguments(GlobeConfig globeConfig, Browser browser) {
    return createChromiumArguments(globeConfig, System.getProperty("chromeoptions.args"));
  }



  protected String[] excludeSwitches(Capabilities capabilities) {
    return hasExtensions(capabilities) ?
      new String[]{"enable-automation"} :
      new String[]{"enable-automation", "load-extension"};
  }

  private boolean hasExtensions(Capabilities capabilities) {
    Map<?, ?> chromeOptions = (Map<?, ?>) capabilities.getCapability("goog:chromeOptions");
    if (chromeOptions == null) return false;

    List<?> extensions = (List<?>) chromeOptions.get("extensions");
    return extensions != null && !extensions.isEmpty();
  }

  private void setMobileEmulation(ChromeOptions chromeOptions) {
    Map<String, Object> mobileEmulation = mobileEmulation();
    if (!mobileEmulation.isEmpty()) {
      chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);
    }
  }



  protected Map<String, Object> mobileEmulation() {
    String mobileEmulation = System.getProperty("chromeoptions.mobileEmulation", "");
    return parsePreferencesFromString(mobileEmulation);
  }


}
