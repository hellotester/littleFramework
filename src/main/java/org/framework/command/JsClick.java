package org.framework.command;

import com.google.auto.service.AutoService;
import org.framework.Command;
import org.framework.JavaScript;
import org.framework.WebElementFinder;

import javax.annotation.Nonnull;

@AutoService(Command.class)
public class JsClick extends MouseCommand {
//    @Nonnull
//    @Override
//    public String commandName() {
//        return "jsClick";
//    }

    @Override
    void onclick(WebElementFinder elementFinder) {
        JavaScript.Click.execute(elementFinder.driver(), elementFinder.findElement());
    }
}
