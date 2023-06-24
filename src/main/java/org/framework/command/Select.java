package org.framework.command;

import com.google.auto.service.AutoService;
import org.checkerframework.checker.units.qual.C;
import org.framework.Command;
import org.framework.JavaScript;
import org.framework.Wait;
import org.framework.WebElementFinder;
import org.framework.util.StringUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

@AutoService(Command.class)
public class Select implements Command<Void> {

    Logger log = LoggerFactory.getLogger(Select.class);

    @Override
    public Void execute(Object proxy, WebElementFinder locator, @Nullable Object... args) throws Exception {
        log.debug("执行select命令 args：{}", Arrays.toString(args));
        WebElement element = locator.findElement();
        // 两种情况 1 原生select 2 模拟下拉框
        Object params = checkAndGetParams(args);
        if (StringUtil.isEqualsIgnoreCase(element.getTagName(), "select")) {
            if (params instanceof Integer) {
                new org.openqa.selenium.support.ui.Select(element).selectByIndex((int) params);
            }
            new org.openqa.selenium.support.ui.Select(element).selectByVisibleText(params.toString());
        } else {
            // 非原生的 参数只能是定位表达式且不能用index 然后查找离下拉框最近的
            if (params instanceof Integer) {
                log.error("非原生select 不支持index 选择");
                throw new IllegalArgumentException("非原生select 不支持index 选择");
            }
            By xpath = By.xpath(String.format("//*[text()='%s']", params));
            WebElement option = getMinGapElementToSelect(locator.driver(), xpath, element);
            JavaScript.scrollElementForView.execute(locator.driver(), option);
            Wait.waitElementClickable(option).click();
        }

        return null;
    }

    Object checkAndGetParams(Object[] args) {
        if (args == null || args.length < 1 || args[0] == null) {
            throw new IllegalArgumentException("must be one parameter and can't be null.");
        }

        return args[0];
    }

    WebElement getMinGapElementToSelect(WebDriver driver, By by, WebElement select) {
        List<WebElement> webElements = Wait.waitAllElementsExist(driver, by);
        if (webElements.size() == 1) {
            return webElements.get(0);
        }
        return webElements.stream().filter(element -> element.getLocation().y > select.getLocation().y)
                .min((f, l) -> Math.min(f.getLocation().y, l.getLocation().y)).orElse(null);
    }
}
