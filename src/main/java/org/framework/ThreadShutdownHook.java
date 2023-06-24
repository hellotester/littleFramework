package org.framework;

import org.openqa.selenium.WebDriver;

public class ThreadShutdownHook extends Thread {
    GlobeConfig globeConfig;
    WebDriver driver;

    DownloadsFolder downloadsFolder;
    WebDriverSweeper sweeper;

    public ThreadShutdownHook(GlobeConfig globeConfig, WebDriver driver, DownloadsFolder downloadsFolder) {
        this(globeConfig, driver, downloadsFolder, new WebDriverSweeper());
    }

    public ThreadShutdownHook(GlobeConfig globeConfig, WebDriver driver, DownloadsFolder downloadsFolder, WebDriverSweeper sweeper) {
        this.driver = driver;
        this.globeConfig = globeConfig;
        this.downloadsFolder = downloadsFolder;
        this.sweeper = sweeper;
    }

    @Override
    public void run() {
        sweeper.close(globeConfig, driver);
        downloadsFolder.cleanupBeforeDownload();
    }
}
