package org.framework.webdriver;

import static org.openqa.selenium.remote.Browser.*;

public class Browser {
    public final String name;
    public final boolean headless;

    public Browser(String name, boolean headless) {
        this.name = name;
        this.headless = headless;
    }


    public boolean isHeadless() {
        return headless;
    }


    public boolean isChrome() {
        return CHROME.is(name);
    }


    public boolean isFirefox() {
        return FIREFOX.is(name);
    }


    public boolean isIE() {
        return IE.is(name) || "ie".equalsIgnoreCase(name);

    }


    public boolean isEdge() {
        return EDGE.is(name) || "edge".equalsIgnoreCase(name);
    }


    public boolean isOpera() {
        return OPERA.is(name);
    }


    public boolean isSafari() {
        return SAFARI.is(name);
    }


    public boolean supportsInsecureCerts() {
        return !isIE() && !isSafari();
    }
}

