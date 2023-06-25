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

    public long getRetryCount() {
        return retryCount;
    }

    public long getToClickableTimeout() {
        return waitForClickTimeout;
    }

    public long getWaitForVisibleTimeout() {
        return waitForVisibleTimeout;
    }

    public long getWaitForAjaxTimeout() {
        return waitForAjaxTimeout;
    }

    public long getPollingInterval() {
        return pollingInterval;
    }

    @Value("${timeout.pageLoadTimeout:30000}")
    long pageLoadTimeout;
    @Value("${timeout.javaScriptExecuteTimeout:1000}")
    long javaScriptExecuteTimeout;
    @Value("${timeout.searchDomContentTimeout:15000}")
    long searchDomContentTimeout;

    @Value("${timeout.waitForClickTimeout:5000}")
    long waitForClickTimeout;


    @Value("${timeout.waitForVisibleTimeout:5000}")
    long waitForVisibleTimeout;


    @Value("${waitAjaxTimeout:8000}")
    long waitForAjaxTimeout;


    @Value("${timeout.retryTimeout:5000}")
    long retryCount;
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
                ", actionsTimeout=" + retryCount +
                ", pollingInterval=" + pollingInterval +
                '}';
    }
}
