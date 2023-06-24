package org.framework;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Command<T> {

    T execute(Object proxy, WebElementFinder locator, @Nullable Object... args) throws Exception;

//    @Nonnull
//    String commandName();

}
