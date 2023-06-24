package org.framework.command;

import com.google.auto.service.AutoService;
import org.framework.Command;
import org.framework.ElementProxy;
import org.framework.WebElementFinder;
import org.framework.ui.WrapEleUI;
import org.framework.util.ClassUtil;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Proxy;


@AutoService(Command.class)
public class GetSibling implements Command<WrapEleUI> {

    @Override
    @Nonnull
    public WrapEleUI execute(Object proxy, WebElementFinder locator, @Nullable Object... args) {
        int index = 1;
        if (args != null && args.length > 0 && args[0] != null) {
            index = (int) args[0];
        }
        By xpath;
        if (index >= 0) {
            xpath = By.xpath(String.format("following-sibling::*[%d]", index));
        } else {
            xpath = By.xpath(String.format("preceding-sibling::*[%d]", Math.abs(index)));
        }
        return (WrapEleUI) Proxy.newProxyInstance(ClassUtil.getClassLoad(), new Class[]{WrapEleUI.class},
                new ElementProxy(WebElementFinder.with(locator.driver(), locator, xpath, 0, null)));
    }

}
