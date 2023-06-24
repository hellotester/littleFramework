package org.framework.command;


import org.framework.Command;
import org.framework.WebElementFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Arrays;

public abstract class MouseCommand implements Command<Void> {

    Logger log = LoggerFactory.getLogger(MouseCommand.class);

    @Override
    public Void execute(Object proxy, WebElementFinder webElementFinder, @Nullable Object... args) {
        log.debug("执行鼠标命令 args {}", Arrays.toString(args));
        onclick(webElementFinder);
        return null;
    }

    abstract void onclick(WebElementFinder elementFinder);

}
