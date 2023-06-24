package org.framework.command;

import org.framework.Command;
import org.framework.WebElementFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;

public abstract class KeyboardCommand implements Command<Void> {

    Logger log = LoggerFactory.getLogger(KeyboardCommand.class);
    @Override
    public Void execute(Object proxy, WebElementFinder locator, @Nullable Object... args) throws Exception {
        log.debug("执行键盘命令 args {}", Arrays.toString(args));
        if (Objects.isNull(args) || args.length == 0) {
            throw new IllegalArgumentException("Keys to send should be a not null CharSequence");
        }
        StringBuilder builder = new StringBuilder();
        for (Object arg : args) {
            builder.append(arg);
        }
        type(locator, builder);
        return null;
    }

    abstract void type(WebElementFinder elementFinder, CharSequence str);
}
