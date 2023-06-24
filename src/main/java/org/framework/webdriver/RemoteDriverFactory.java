package org.framework.webdriver;

import org.framework.GlobeConfig;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;


public class RemoteDriverFactory {
  public WebDriver create(GlobeConfig globeConfig, MutableCapabilities capabilities) {
    try {
      RemoteWebDriver webDriver = new RemoteWebDriver(new URL(globeConfig.remote()), capabilities);
      webDriver.setFileDetector(new LocalFileDetector());
      return webDriver;
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("Invalid 'remote' parameter: " + globeConfig.remote(), e);
    }
  }
}
