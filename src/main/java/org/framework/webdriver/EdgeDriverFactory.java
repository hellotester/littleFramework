package org.framework.webdriver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.framework.GlobeConfig;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.edge.EdgeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;

import static org.openqa.selenium.remote.CapabilityType.ACCEPT_INSECURE_CERTS;


public class EdgeDriverFactory extends AbstractChromiumDriverFactory {
    private static final Logger log = LoggerFactory.getLogger(EdgeDriverFactory.class);


    public void setupWebDriverBinary() {
        if (isSystemPropertyNotSet("webdriver.edge.driver")) {
            WebDriverManager.edgedriver().setup();
        }
    }


    public WebDriver create(GlobeConfig globeConfig, Browser browser, @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
        EdgeOptions options = createCapabilities(globeConfig, browser, proxy, browserDownloadsFolder);
        EdgeDriverService driverService = createDriverService(globeConfig);
        return new EdgeDriver(driverService, options);
    }

    private EdgeDriverService createDriverService(GlobeConfig globeConfig) {
        return withLog(globeConfig, new EdgeDriverService.Builder());
    }


    public EdgeOptions createCapabilities(GlobeConfig globeConfig, Browser browser,
                                          @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
        MutableCapabilities capabilities = createCommonCapabilities(new EdgeOptions(), globeConfig, browser, proxy);
        capabilities.setCapability(ACCEPT_INSECURE_CERTS, true);

        EdgeOptions options = new EdgeOptions().merge(capabilities);
        options.setHeadless(globeConfig.headless());

        if (!globeConfig.browserBinary().isEmpty()) {
            log.info("Using browser binary: {}", globeConfig.browserBinary());
            log.warn("Changing browser binary not supported in Edge, setting will be ignored.");
        }

        options.addArguments(createEdgeArguments(globeConfig));
        options.setExperimentalOption("prefs", prefs(browserDownloadsFolder, System.getProperty("edgeoptions.prefs", "")));
        return options;
    }


    protected List<String> createEdgeArguments(GlobeConfig globeConfig) {
        return createChromiumArguments(globeConfig, System.getProperty("edgeoptions.args"));
    }
}
