package org.framework.emun;

import org.framework.Value;
import org.framework.util.ClassUtil;

public enum TestStrategy {

    TEST_STRATEGY;

    TestStrategy() {
        try {
            ClassUtil.injectFieldFromYml(this);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }



    public String getBaseUrl() {
        return baseUrl;
    }

    public boolean isHoldBrowserOpen() {
        return holdBrowserOpen;
    }

    public boolean isReopenBrowserOnFail() {
        return reopenBrowserOnFail;
    }

    public boolean isFailScreenshots() {
        return failScreenshots;
    }

    public boolean isActionFailRetry() {
        return actionFailRetry;
    }
    @Value("${testStrategy.baseUrl:}")
    String baseUrl;
    @Value("testStrategy.holdBrowserOpen")
    boolean holdBrowserOpen;
    @Value("testStrategy.reopenBrowserOnFail")
    boolean reopenBrowserOnFail;
    @Value("testStrategy.failScreenshots")
    boolean failScreenshots;
    @Value("testStrategy.actionFailRetry")
    boolean actionFailRetry;

    public static TestStrategy getInstance() {
        return TEST_STRATEGY;
    }

}
