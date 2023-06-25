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

@AutoService(Command.class)
public class DoubleClick extends MouseCommand {
    Logger log = LoggerFactory.getLogger(DoubleClick.class);

    @Override
    void onclick(WebElementFinder elementFinder) {
        log.debug("doubleClicking {}", elementFinder.alias());
        try {
            Wait.unit(elementFinder::findElement, WebElement::isDisplayed, Timeout.Timeout.getWaitForVisibleTimeout());
            new Actions(elementFinder.driver()).doubleClick(Wait.unit(elementFinder::findElement, WebElement::isEnabled, Timeout.Timeout.getToClickableTimeout()));
        } catch (ConditionTimeoutException timeoutException) {
            throw new NoSuchElementException(elementFinder.alias());
        }
    }
}
