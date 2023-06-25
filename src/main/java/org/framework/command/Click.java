package org.framework.command;

import com.google.auto.service.AutoService;
import org.framework.Command;
import org.framework.Wait;
import org.framework.WebElementFinder;
import org.framework.emun.Timeout;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AutoService(Command.class)
public class Click extends MouseCommand {

    Logger log = LoggerFactory.getLogger(Click.class);

    @Override
    void onclick(WebElementFinder elementFinder) {
        Wait.unit(elementFinder::findElement, WebElement::isDisplayed, Timeout.Timeout.getWaitForVisibleTimeout());
        Wait.unit(elementFinder::findElement, WebElement::isEnabled, Timeout.Timeout.getToClickableTimeout()).click();
    }

}
