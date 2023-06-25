package org.framework;

import org.apache.commons.io.IOUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public enum JavaScript {

    Click {
        @Override
        public Object execute(WebDriver driver, Object... args) {
            checkArgsLength(args, 1);
            run((JavascriptExecutor) driver, getScript("click.js"), args[0]);
            return null;
        }
    },

    SetAttribute {
        @Override
        public Object execute(WebDriver driver, Object... args) {
            checkArgsLength(args, 3);
            run((JavascriptExecutor) driver, getScript("setAttribute.js"), args[0], args[1], args[2]);
            return null;
        }
    },


    scrollElementForView {
        @Override
        public Object execute(WebDriver driver, Object... args) {
            checkArgsLength(args, 1);
            run((JavascriptExecutor) driver, getScript("scrollView.js"), args[0]);
            return null;
        }
    },

    findElementByStr {
        @Override
        public Object execute(WebDriver driver, Object... args) {
            checkArgsLength(args, 1);
            return run((JavascriptExecutor) driver, getScript("find.js"), args[0]);
        }
    },

    waitForAjax {
        @Override
        public Object execute(WebDriver driver, Object... args) {
            return run((JavascriptExecutor) driver, getScript("waitForAjax.js"));
        }
    },


    waitForAjaxUrl {
        @Override
        public Object execute(WebDriver driver, Object... args) {
            checkArgsLength(args, 1);
            return  run((JavascriptExecutor) driver, getScript("waitForAjaxRequestUrl.js"), args[0]);
        }
    },


    ;


    protected String getScript(String name) {
        try {
            InputStream inputStream = JavaScript.class.getClassLoader().getResourceAsStream("js/" + name);
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("js file to str err", e);
        } catch (NullPointerException e) {
            throw new RuntimeException("notfound file:" + name);
        }


    }

    protected void checkArgsLength(Object[] args, int length) {
        if (length > 0) {
            if (args == null || args.length != length) {
                throw new IllegalArgumentException(String.format("must be %d parameter and can't be null. but was %s", length, Arrays.toString(args)));
            }
        }

    }

    protected Object run(JavascriptExecutor executor, String script, Object... args) {
        return executor.executeScript(script, args);
    }


    public abstract Object execute(WebDriver driver, Object... args);
}
