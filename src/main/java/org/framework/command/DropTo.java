package org.framework.command;


import com.google.auto.service.AutoService;
import org.framework.Command;
import org.framework.Wait;
import org.framework.WebElementFinder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@AutoService(Command.class)
public class DropTo implements Command<Void> {

    @Override
    @Nonnull
    public Void execute(Object proxy, WebElementFinder locator, @Nullable Object... args) {
        WebElementFinder target = findTarget(locator.driver(), args);
        Wait.unit(() -> locator.findElement().isDisplayed());
        Wait.unit(() -> target.findElement().isDisplayed());
        dropUsingActions(locator.driver(), locator.findElement(), target.findElement());
        return null;
    }

    @Nonnull
    protected WebElementFinder findTarget(WebDriver driver, @Nullable Object[] args) {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("Missing target argument");
        } else if (args[0] instanceof String) {
            return WebElementFinder.with(driver, By.cssSelector((String) args[0]));
        } else {
            throw new IllegalArgumentException("Unknown target type: " + args[0] +
                    " (only String or WebElement are supported)");
        }
    }

    private void dropUsingActions(WebDriver driver, WebElement from, WebElement target) {
        new Actions(driver).dragAndDrop(from, target).perform();
    }


}
