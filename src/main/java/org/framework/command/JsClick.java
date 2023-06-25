package org.framework.command;

import com.google.auto.service.AutoService;
import org.framework.Command;
import org.framework.JavaScript;
import org.framework.WebElementFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

@AutoService(Command.class)
public class JsClick extends MouseCommand {
    //    @Nonnull
//    @Override
//    public String commandName() {
//        return "jsClick";
//    }
    Logger log = LoggerFactory.getLogger(JsClick.class);

    @Override
    void onclick(WebElementFinder elementFinder) {
        log.debug("clicking {} by js", elementFinder.alias());
        JavaScript.Click.execute(elementFinder.driver(), elementFinder.findElement());
    }
}
