package org.framework.command;

import com.google.auto.service.AutoService;
import org.framework.Command;
import org.framework.Wait;
import org.framework.WebElementFinder;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AutoService(Command.class)
public class Type extends KeyboardCommand {

    Logger log = LoggerFactory.getLogger(Type.class);


    @Override
    void type(WebElementFinder elementFinder, CharSequence str) {
        log.debug("typing {} in the {}", str, elementFinder.alias());
        Wait.unit(elementFinder::findElement, WebElement::isDisplayed);
        Wait.unit(elementFinder::findElement, WebElement::isEnabled).sendKeys(str);
    }
}
