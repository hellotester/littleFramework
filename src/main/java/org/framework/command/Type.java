package org.framework.command;

import com.google.auto.service.AutoService;
import org.framework.Command;
import org.framework.Wait;
import org.framework.WebElementFinder;
import org.framework.util.StringUtil;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

@AutoService(Command.class)
public class Type extends KeyboardCommand {

    Logger log = LoggerFactory.getLogger(Type.class);


    @Override
    void type(WebElementFinder elementFinder, CharSequence str) {
        elementFinder.findElement();
        Wait.unit(elementFinder::findElement, WebElement::isEnabled).sendKeys(str);
    }
}
