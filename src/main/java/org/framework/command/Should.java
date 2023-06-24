package org.framework.command;

import com.google.auto.service.AutoService;
import org.framework.Command;
import org.framework.Conj;
import org.framework.Wait;
import org.framework.WebElementFinder;
import org.framework.util.StringUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AutoService(Command.class)
public class Should implements Command<Conj> {
    Logger log = LoggerFactory.getLogger(Should.class);
    Pattern cssValid = Pattern.compile("^css\\.(?<condition>.+)\\..+");

    @Override
    public Conj execute(Object proxy, WebElementFinder locator, @Nullable Object... args) throws Exception {
        log.debug("should command, args :{}", Arrays.toString(args));

        if (args == null || args.length < 2) {
            log.error("example: el.should(attribute.be, attributeValue)");
            throw new IllegalArgumentException("the parameter length is not valid. Two strings are required");
        }
        if (!(args[0] instanceof String)) {
            throw new IllegalArgumentException("the parameter must be string.");
        }
        String arg = (String) args[0];

        Result result;

        Matcher matcher = cssValid.matcher(arg);
        String condition;
        Object conditionVal = args[1];
        if (matcher.matches()) {
            condition = matcher.group("condition");
            result = new Result(conditionVal, beCssAttribute(locator.driver(), locator.selocator(), condition));
        } else if (arg.contains(".")) {
            condition = arg.substring(0, arg.indexOf("."));// xx.yy
            result = new Result(conditionVal, beAttribute(locator.driver(), locator.selocator(), condition));
        } else {
            throw new IllegalArgumentException("parameter must look like attribute.be or attribute.not" +
                    "\n" +
                    "example: el.should(attribute.be, attributeValue)");
        }

        if (arg.matches(".+\\.be.*")) {
            if (!Objects.equals(result.compareResult, result.realResult)) {
                throw new AssertionError(String.format("should %s be %s ,but was got %s", condition, conditionVal, result.realResult));
            }

        } else if (arg.matches(".+\\.not.*")) {
            if (Objects.equals(result.compareResult, result.realResult)) {
                throw new AssertionError(String.format("should %s not '%s' ,but was got '%s'", condition, conditionVal, result.realResult));
            }
        } else if (arg.matches(".+\\.contains.*")) {
            if ((result.realResult == null && result.compareResult != null) || (result.realResult != null && result.compareResult == null)) {
                throw new AssertionError(String.format("should %s contains '%s' ,but was got '%s'", condition, conditionVal, result.realResult));
            }
            if (result.realResult != null) {
                if (!(result.realResult.contains(result.compareResult + ""))) {
                    throw new AssertionError(String.format("should %s contains '%s' ,but was got '%s'", condition, conditionVal, result.realResult));
                }
            }
        }

        return (Conj) proxy;
    }


    String beAttribute(WebDriver driver, By by, String name) {
        return Wait.waitElementExist(driver, by).getAttribute(name);

    }


    String beCssAttribute(WebDriver driver, By by, String name) {
        return rgbToHex(Wait.waitElementExist(driver, by).getCssValue(name));
    }

    static String rgbToHex(String str) {
        Pattern rgb = Pattern.compile("^rgba\\((?<num>\\d+,\\d+,\\d+),\\d+\\)");
        String input = str.replaceAll("\\s", "");
        System.out.println(input);
        Matcher matcher = rgb.matcher(input);

        if (matcher.matches()) {
            StringBuilder hex = new StringBuilder("#");
            for (String num : matcher.group("num").split(",")) {
                hex.append(Integer.toHexString(Integer.parseInt(num)));
            }
            return hex.toString();
        }
        return str;

    }

    public static void main(String[] args) {
        System.out.println(rgbToHex("rgba(78, 110, 242, 1)"));
        ;
    }

    static class Result {


        Object compareResult;
        String realResult;


        public Result(Object compareResult, String realResult) {
            this.compareResult = compareResult;
            this.realResult = realResult;
        }
    }
}
