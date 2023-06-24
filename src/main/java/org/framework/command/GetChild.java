package org.framework.command;

import com.google.auto.service.AutoService;
import org.framework.Command;
import org.framework.ElementProxy;
import org.framework.WebElementFinder;
import org.framework.ui.WrapEleUI;
import org.framework.util.ClassUtil;
import org.openqa.selenium.By;

import javax.annotation.Nullable;
import java.lang.reflect.Proxy;

@AutoService(Command.class)
public class GetChild implements Command<WrapEleUI> {
    @Override
    public WrapEleUI execute(Object proxy, WebElementFinder locator, @Nullable Object... args) throws Exception {
        if (args == null || args.length < 1) {
            throw new IllegalArgumentException("must be one parameters.");
        }
        if (!(args[0] instanceof String)) {
            throw new IllegalArgumentException("the parameter can only be string.");
        }
        String arg = (String) args[0];
        String[] conditions = arg.split("&");// get xx=xx,yy=yy
        StringBuilder attrs = new StringBuilder("[");
        for (String condition : conditions) {
            if (!condition.matches(".+='.+'")) {
                throw new IllegalArgumentException("parameter format error, should be [attribute='attributeValue'] but got:" + condition);
            }
            if (condition.matches(".+\\^='.+'")) {
                String[] split = condition.split("\\^=");
                attrs.append("startwith(@").append(split[0]).append(",").append(split[1]).append(")").append(" and ");
                continue;
            }
            attrs.append("@").append(condition).append(" and ");
        }
        attrs.replace(attrs.lastIndexOf(" and "), attrs.length(), "");
        attrs.append("]");

        By xpath = By.xpath("descendant::*" + attrs);
        System.out.println(xpath);
        return (WrapEleUI) Proxy.newProxyInstance(ClassUtil.getClassLoad(), new Class[]{WrapEleUI.class},
                new ElementProxy(WebElementFinder.with(locator.driver(), locator, xpath, 0, null)));
    }


}
