package org.framework.command;


import com.google.auto.service.AutoService;
import org.framework.Command;
import org.framework.JavaScript;
import org.framework.WebElementFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Arrays;

@AutoService(Command.class)
public class SetAttribute implements Command<Void> {
    Logger log = LoggerFactory.getLogger(SetAttribute.class);

    @Override
    public Void execute(Object proxy, WebElementFinder locator, @Nullable Object... args) throws Exception {
        log.debug("执行setAttribute命令, args:{}", Arrays.toString(args));
        Object[] params = checkAndGetParams(args);
        JavaScript.SetAttribute.execute(locator.driver(), locator.findElement(), params);
        return null;
    }

    Object[] checkAndGetParams(Object[] objects) {
        if (objects == null || objects.length < 2) {
            throw new IllegalArgumentException("must params");
        }
        if (objects[0] == null || objects[1] == null) {
            throw new IllegalArgumentException("must params");
        }
        return new Object[]{objects[0], objects[1]};
    }
}
