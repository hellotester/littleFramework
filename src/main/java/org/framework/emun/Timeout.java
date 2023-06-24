package org.framework.emun;

import org.framework.Value;
import org.framework.util.ClassUtil;

public enum Timeout {
    Timeout;

    Timeout() {
        try {
            ClassUtil.injectFieldFromYml(this);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public long getPageLoadTimeout() {
        return pageLoadTimeout;
    }

    public long getJavaScriptExecuteTimeout() {
        return javaScriptExecuteTimeout;
    }

    public long getSearchDomContentTimeout() {
        return searchDomContentTimeout;
    }

    public long getRetryTimeoutTimeout() {
        return retryTimeout;
    }

    public long getToClickableTimeout() {
        return toClickableTimeout;
    }

    public long getToVisibleTimeout() {
        return toVisibleTimeout;
    }

    public long getPollingInterval() {
        return pollingInterval;
    }

    @Value("${timeout.pageLoadTimeout:30000}")
    long pageLoadTimeout;
    @Value("${timeout.javaScriptExecuteTimeout:1000}")
    long javaScriptExecuteTimeout;
    @Value("${timeout.searchDomContentTimeout:5000}")
    long searchDomContentTimeout;

    @Value("${timeout.waitForClickTimeout:5000}")
    long waitForClickTimeout;

    @Value("${timeout.waitForVisibleTimeout:5000}")
    long waitForVisibleTimeout;

    @Value("${timeout.toClickableTimeout:5000}")
    long toClickableTimeout;

    @Value("${timeout.toVisibleTimeout:5000}")
    long toVisibleTimeout;

    @Value("${timeout.retryTimeout:5000}")
    long retryTimeout;
    @Value("${timeout.pollingInterval:500}")
    long pollingInterval;


    public static Timeout getInstance() {
        return Timeout;
    }

    @Override
    public String toString() {
        return "Timeout{" +
                "pageLoadTimeout=" + pageLoadTimeout +
                ", javaScriptExecuteTimeout=" + javaScriptExecuteTimeout +
                ", searchDomContentTimeout=" + searchDomContentTimeout +
                ", actionsTimeout=" + retryTimeout +
                ", pollingInterval=" + pollingInterval +
                '}';
    }
}
