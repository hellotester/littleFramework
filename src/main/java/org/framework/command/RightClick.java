package org.framework.command;

import com.google.auto.service.AutoService;
import org.framework.Command;
import org.framework.Wait;
import org.framework.WebElementFinder;
import org.openqa.selenium.interactions.Actions;

import javax.annotation.Nonnull;
@AutoService(Command.class)
public class RightClick extends MouseCommand{
//    @Nonnull
//    @Override
//    public String commandName() {
//        return "rightClick";
//    }

    @Override
    void onclick(WebElementFinder elementFinder) {
        new Actions(elementFinder.driver())
                .contextClick(Wait.waitElementClickable(elementFinder.findElement()));
    }
}
