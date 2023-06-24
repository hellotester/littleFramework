package org.framework;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

import static java.lang.System.currentTimeMillis;


public class WebDriverSweeper {
    private static final Logger log = LoggerFactory.getLogger(WebDriverSweeper.class);

    public void close(GlobeConfig globeConfig, @Nullable WebDriver webDriver) {
        long threadId = Thread.currentThread().getId();
        if (globeConfig.holdBrowserOpen()) {
            log.info("Hold browser and proxy open: {} -> {}", threadId, webDriver);
            return;
        }

        if (webDriver != null) {
            long start = currentTimeMillis();
            log.info("Close webdriver: {} -> {}...", threadId, webDriver);
            close(webDriver);
            log.info("Closed webdriver {} in {} ms", threadId, currentTimeMillis() - start);
        }
    }

    private void close(WebDriver webdriver) {
        try {
            webdriver.quit();
        } catch (UnreachableBrowserException e) {
            // It happens for Firefox. It's ok: browser is already closed.
            log.debug("Browser is unreachable", e);
        } catch (WebDriverException cannotCloseBrowser) {
            log.error("Cannot close browser.", cannotCloseBrowser);
        } catch (RuntimeException e) {
            log.error("Cannot close browser", e);
        }
    }
}
