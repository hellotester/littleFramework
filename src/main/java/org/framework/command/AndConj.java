package org.framework.command;

import com.google.auto.service.AutoService;
import org.framework.Command;
import org.framework.WebElementFinder;

import javax.annotation.Nullable;

@AutoService(Command.class)
public class AndConj implements Command<org.framework.Should> {
    @Override
    public org.framework.Should execute(Object proxy, WebElementFinder locator, @Nullable Object... args) throws Exception {
        return (org.framework.Should) proxy;
    }
}
