package org.framework.command;


import com.google.auto.service.AutoService;
import org.framework.Command;
import org.framework.Wait;
import org.framework.WebElementFinder;
import org.openqa.selenium.interactions.Actions;

@AutoService(Command.class)
public class DoubleClick extends MouseCommand {
    @Override
    void onclick(WebElementFinder elementFinder) {
        new Actions(elementFinder.driver()).doubleClick(Wait.waitElementClickable(elementFinder.findElement()));
    }
}
