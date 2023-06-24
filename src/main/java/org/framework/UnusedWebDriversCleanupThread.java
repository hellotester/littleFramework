package org.framework;


import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

class UnusedWebDriversCleanupThread extends Thread {
  private static final Logger log = LoggerFactory.getLogger(UnusedWebDriversCleanupThread.class);

  private final Collection<Thread> allWebDriverThreads;
  private final Map<Long, WebDriver> threadWebDriver;
  private final Map<Long, DownloadsFolder> threadDownloadsFolder;

  UnusedWebDriversCleanupThread(Collection<Thread> allWebDriverThreads,
                                Map<Long, WebDriver> threadWebDriver,
                                Map<Long, DownloadsFolder> threadDownloadsFolder) {
    this.allWebDriverThreads = allWebDriverThreads;
    this.threadWebDriver = threadWebDriver;
    this.threadDownloadsFolder = threadDownloadsFolder;
    setDaemon(true);
    setName("Webdrivers killer thread");
  }

  @Override
  public void run() {
    while (true) {
      closeUnusedWebdrivers();
      try {
        Thread.sleep(100);
      }
      catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      }
    }
  }

  private void closeUnusedWebdrivers() {
    for (Thread thread : allWebDriverThreads) {
      if (!thread.isAlive()) {
        log.info("Thread {} is dead. Let's close webdriver {}", thread.getId(), threadWebDriver.get(thread.getId()));
        closeWebDriver(thread);
      }
    }
  }

  private void closeWebDriver(Thread thread) {
    allWebDriverThreads.remove(thread);
    WebDriver driver = threadWebDriver.remove(thread.getId());

    if (driver == null) {
      log.info("No webdriver found for thread: {} - nothing to close", thread.getId());
    }
    else {
      quitSafely(thread.getId(), driver);
    }

    threadDownloadsFolder.remove(thread.getId());
  }

  private void quitSafely(long threadId, WebDriver driver) {
    try {
      driver.quit();
    }
    catch (NoSuchSessionException e) {
      log.debug("Webdriver for thread {} has been closed meanwhile", threadId, e);
    }
    catch (WebDriverException e) {
      if ("The driver server has unexpectedly died!".equalsIgnoreCase(e.getMessage())) {
        log.debug("Webdriver for thread {} has been closed meanwhile", threadId, e);
      }
      else {
        log.error("Failed to close webdriver for thread {}", threadId, e);
      }
    }
  }
}

