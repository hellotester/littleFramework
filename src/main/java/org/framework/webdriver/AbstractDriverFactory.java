package org.framework.webdriver;

import org.framework.DriverFactory;
import org.framework.GlobeConfig;
import org.framework.StaticResource;
import org.framework.util.YamlUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.service.DriverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.framework.util.FileUtil.ensureFolderExists;
import static org.openqa.selenium.remote.CapabilityType.*;


public abstract class AbstractDriverFactory implements DriverFactory {
    private static final Logger log = LoggerFactory.getLogger(AbstractDriverFactory.class);
    private static final Pattern REGEX_SIGNED_INTEGER = Pattern.compile("^-?\\d+$");
    private static final Pattern REGEX_VERSION = Pattern.compile("(\\d+)(\\..*)?");


    protected File webDriverLog(GlobeConfig globeConfig) {
        File logFolder = ensureFolderExists(new File(globeConfig.webDriverLogFolder()).getAbsoluteFile());
        String logFileName = String.format("webdriver.%s.log", LocalDateTime.now().toString().replaceAll(":", "_"));
        File logFile = new File(logFolder, logFileName).getAbsoluteFile();
        log.info("Write webdriver logs to: {}", logFile);
        return logFile;
    }

    protected <DS extends DriverService, B extends DriverService.Builder<DS, ?>> DS withLog(GlobeConfig globeConfig, B dsBuilder) {
        if (globeConfig.webDriverLogsEnabled()) {
            dsBuilder.withLogFile(webDriverLog(globeConfig));
        }
        return dsBuilder.build();
    }


    protected MutableCapabilities createCommonCapabilities(GlobeConfig globeConfig, Browser browser, @Nullable Proxy proxy) {
        return createCommonCapabilities(new MutableCapabilities(), globeConfig, browser, proxy);
    }


    protected <T extends MutableCapabilities> T createCommonCapabilities(T capabilities, GlobeConfig globeConfig, Browser browser, @Nullable Proxy proxy) {
        if (proxy != null) {
            capabilities.setCapability(PROXY, proxy);
        }
        if (globeConfig.browserVersion() != null && !globeConfig.browserVersion().isEmpty()) {
            capabilities.setCapability(BROWSER_VERSION, globeConfig.browserVersion());
        }
        capabilities.setCapability(PAGE_LOAD_STRATEGY, globeConfig.pageLoadStrategy());
        capabilities.setCapability(ACCEPT_SSL_CERTS, true);

        if (browser.supportsInsecureCerts()) {
            capabilities.setCapability(ACCEPT_INSECURE_CERTS, true);
        }
        capabilities.setCapability(SUPPORTS_JAVASCRIPT, true);
        capabilities.setCapability(TAKES_SCREENSHOT, true);
        capabilities.setCapability(SUPPORTS_ALERTS, true);

        transferCapabilitiesFromSystemProperties(capabilities);

        return capabilities;
    }

    @SuppressWarnings("unchecked")
    protected <T extends MutableCapabilities> T merge(T capabilities, MutableCapabilities additionalCapabilities) {
        verifyItsSameBrowser(capabilities, additionalCapabilities);
        return (T) capabilities.merge(additionalCapabilities);
    }

    private void verifyItsSameBrowser(Capabilities base, Capabilities extra) {
        if (areDifferent(base.getBrowserName(), extra.getBrowserName())) {
            throw new IllegalArgumentException(String.format("Conflicting browser name: '%s' vs. '%s'",
                    base.getBrowserName(), extra.getBrowserName()));
        }
    }

    private boolean areDifferent(String text1, String text2) {
        return !text1.isEmpty() && !text2.isEmpty() && !text1.equals(text2);
    }

    protected void transferCapabilitiesFromSystemProperties(MutableCapabilities currentBrowserCapabilities) {
        String prefix = "capabilities.";
        Map<String, Object> map = YamlUtil.loadYaml(StaticResource.source);
        for (String key : map.keySet()) {
            if (key.startsWith(prefix)) {
                String capability = key.substring(prefix.length());
                String value = String.valueOf(map.get(key));
                log.debug("Use {}={}", key, value);
                currentBrowserCapabilities.setCapability(capability, convertStringToNearestObjectType(value));
            }
        }
    }

    /**
     * 将字符串转换为布尔整数或返回原始字符串。
     *
     * @param value string to convert
     * @return string's object representation
     */
    protected Object convertStringToNearestObjectType(String value) {
        if (isBoolean(value)) {
            return Boolean.valueOf(value);
        } else if (isInteger(value)) {
            return parseInt(value);
        } else {
            return value;
        }
    }


    protected boolean isInteger(String value) {
        return REGEX_SIGNED_INTEGER.matcher(value).matches();
    }


    protected boolean isBoolean(String value) {
        return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
    }


    protected boolean isSystemPropertyNotSet(String key) {
        return isBlank(System.getProperty(key, ""));
    }


    protected int majorVersion(@Nullable String browserVersion) {
        if (isBlank(browserVersion)) return 0;
        Matcher matcher = REGEX_VERSION.matcher(browserVersion);
        return matcher.matches() ? parseInt(matcher.replaceFirst("$1")) : 0;
    }

    @SuppressWarnings("unchecked")
    protected <T> T cast(Object value) {
        return (T) value;
    }


}
