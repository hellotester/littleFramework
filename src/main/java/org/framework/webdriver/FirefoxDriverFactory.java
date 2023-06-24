package org.framework.webdriver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.IOUtils;
import org.framework.GlobeConfig;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static java.nio.charset.StandardCharsets.UTF_8;


public class FirefoxDriverFactory extends AbstractDriverFactory {
  private static final Logger log = LoggerFactory.getLogger(FirefoxDriverFactory.class);


  public void setupWebDriverBinary() {
    if (isSystemPropertyNotSet("webdriver.gecko.driver")) {
      WebDriverManager.firefoxdriver().setup();
    }
  }




  public WebDriver create(GlobeConfig globeConfig, Browser browser, @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    return new FirefoxDriver(createDriverService(globeConfig), createCapabilities(globeConfig, browser, proxy, browserDownloadsFolder));
  }



  protected GeckoDriverService createDriverService(GlobeConfig globeConfig) {
    return withLog(globeConfig, new GeckoDriverService.Builder());
  }




  public FirefoxOptions createCapabilities(GlobeConfig globeConfig, Browser browser,
                                           @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    FirefoxOptions initialOptions = new FirefoxOptions();
    initialOptions.setHeadless(globeConfig.headless());
    setupBrowserBinary(globeConfig, initialOptions);
    setupPreferences(initialOptions);

    final FirefoxOptions options = initialOptions.merge(createCommonCapabilities(new FirefoxOptions(), globeConfig, browser, proxy));

    setupDownloadsFolder(options, browserDownloadsFolder);

    Map<String, String> ffProfile = collectFirefoxProfileFromSystemProperties();
    if (!ffProfile.isEmpty()) {
      transferFirefoxProfileFromSystemProperties(options, ffProfile);
    }
    injectFirefoxPrefs(options);
    options.addArguments(globeConfig.browserOption());
    return options;
  }

  protected void setupBrowserBinary(GlobeConfig globeConfig, FirefoxOptions firefoxOptions) {
    if (!globeConfig.browserBinary().isEmpty()) {
      log.info("Using browser binary: {}", globeConfig.browserBinary());
      firefoxOptions.setBinary(globeConfig.browserBinary());
    }
  }

  protected void setupPreferences(FirefoxOptions firefoxOptions) {
    firefoxOptions.addPreference("network.automatic-ntlm-auth.trusted-uris", "http://,https://");
    firefoxOptions.addPreference("network.automatic-ntlm-auth.allow-non-fqdn", true);
    firefoxOptions.addPreference("network.negotiate-auth.delegation-uris", "http://,https://");
    firefoxOptions.addPreference("network.negotiate-auth.trusted-uris", "http://,https://");
    firefoxOptions.addPreference("network.http.phishy-userpass-length", 255);
    firefoxOptions.addPreference("security.csp.enable", false);
    firefoxOptions.addPreference("network.proxy.no_proxies_on", "");
    firefoxOptions.addPreference("network.proxy.allow_hijacking_localhost", true);
  }

  protected void setupDownloadsFolder(FirefoxOptions firefoxOptions, @Nullable File browserDownloadsFolder) {
    if (browserDownloadsFolder != null) {
      firefoxOptions.addPreference("browser.download.dir", browserDownloadsFolder.getAbsolutePath());
    }
    firefoxOptions.addPreference("browser.helperApps.neverAsk.saveToDisk", popularContentTypes());
    firefoxOptions.addPreference("pdfjs.disabled", true);  // disable the built-in viewer
    firefoxOptions.addPreference("browser.download.folderList", 2); // 0=Desktop, 1=Downloads, 2="reuse last location"
  }



  protected String popularContentTypes() {
    try {
      return String.join(";", IOUtils.readLines(getClass().getResourceAsStream("/content-types.properties"), UTF_8));
    }
    catch (IOException e) {
      return "text/plain;text/csv;application/zip;application/pdf;application/octet-stream;" +
        "application/msword;application/vnd.ms-excel;text/css;text/html";
    }
  }



  protected Map<String, String> collectFirefoxProfileFromSystemProperties() {
    String prefix = "firefoxprofile.";

    Map<String, String> result = new HashMap<>();
    for (String key : System.getProperties().stringPropertyNames()) {
      if (key.startsWith(prefix)) {
        String capability = key.substring(prefix.length());
        String value = System.getProperties().getProperty(key);
        result.put(capability, value);
      }
    }

    return result;
  }

  protected void transferFirefoxProfileFromSystemProperties(FirefoxOptions firefoxOptions, Map<String, String> ffProfile) {
    FirefoxProfile profile = Optional.ofNullable(firefoxOptions.getProfile()).orElseGet(FirefoxProfile::new);

    for (Map.Entry<String, String> entry : ffProfile.entrySet()) {
      String capability = entry.getKey();
      String value = entry.getValue();
      log.debug("Use {}={}", capability, value);
      setCapability(profile, capability, value);
    }

    firefoxOptions.setProfile(profile);
  }

  protected void setCapability(FirefoxProfile profile, String capability, String value) {
    if (isBoolean(value)) {
      profile.setPreference(capability, parseBoolean(value));
    }
    else if (isInteger(value)) {
      profile.setPreference(capability, parseInt(value));
    }
    else {
      profile.setPreference(capability, value);
    }
  }

  private void injectFirefoxPrefs(FirefoxOptions options) {
    if (options.getCapability("moz:firefoxOptions") != null) {
      Map<String, Map<String, Object>> mozOptions = cast(options.getCapability("moz:firefoxOptions"));

      if (mozOptions.containsKey("prefs")) {
        for (Map.Entry<String, Object> pref : mozOptions.get("prefs").entrySet()) {
          options.addPreference(pref.getKey(), pref.getValue());
        }
      }
    }
  }
}
