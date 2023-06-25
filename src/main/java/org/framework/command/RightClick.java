package org.framework.command;

import com.google.auto.service.AutoService;
import org.awaitility.core.ConditionTimeoutException;
import org.framework.Command;
import org.framework.Wait;
import org.framework.WebElementFinder;
import org.framework.emun.Timeout;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

@AutoService(Command.class)
public class RightClick extends MouseCommand {
    //    @Nonnull
//    @Override
//    public String commandName() {
//        return "rightClick";
//    }
    Logger log = LoggerFactory.getLogger(RightClick.class);

    @Override
    void onclick(WebElementFinder elementFinder) {
        log.debug("clicking {} by selenium.actions.", elementFinder.alias());
        try {
            Wait.unit(elementFinder::findElement, WebElement::isDisplayed, Timeout.Timeout.getWaitForVisibleTimeout());
            WebElement element = Wait.unit(elementFinder::findElement, WebElement::isEnabled, Timeout.Timeout.getToClickableTimeout());
            new Actions(elementFinder.driver())
                    .contextClick(element);
        } catch (ConditionTimeoutException timeoutException) {
            throw new NoSuchElementException(elementFinder.alias());
        }

    }
}
