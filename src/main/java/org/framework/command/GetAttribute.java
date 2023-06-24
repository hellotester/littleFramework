package org.framework.command;

import com.google.auto.service.AutoService;
import org.framework.Command;
import org.framework.WebElementFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;


@AutoService(Command.class)
public class GetAttribute implements Command<String> {

    Logger log = LoggerFactory.getLogger(GetAttribute.class);
    @Nonnull
    @Override
    public String execute(Object proxy, WebElementFinder locator, @Nullable Object... args) throws Exception {
        log.debug("执行select命令 args：{}", Arrays.toString(args));
        return locator.findElement().getAttribute(checkAndGetParam(args));
    }


    String checkAndGetParam(Object[] objects) {
        if (objects == null || objects.length == 0) {
            throw new IllegalArgumentException("参数错误，必须有一个参数");
        }
        if (objects[0] == null) {
            throw new IllegalArgumentException("参数错误，参数不能是null");
        }
        return objects[0].toString();
    }
}
