package org.framework.command;

import com.google.auto.service.AutoService;
import org.framework.Command;
import org.framework.Wait;
import org.framework.WebElementFinder;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@AutoService(Command.class)
public class Click extends MouseCommand {


    @Override
    void onclick(WebElementFinder elementFinder) {
        Wait.unit(elementFinder::findElement,
                        element -> element.isDisplayed() && element.isEnabled())
                .click();
    }

}
