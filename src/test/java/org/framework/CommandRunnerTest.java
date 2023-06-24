package org.framework;

import org.framework.command.Click;
import org.framework.command.Select;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CommandRunnerTest {


    @InjectMocks
    Select select = new Select();

    @Test
    public void testExecute() throws Exception {
        Select spy = spy(select);
        CommandRunner mock = mock(CommandRunner.class);
        doReturn(select).when(mock).load("select");
        doNothing().when(spy).execute(any(), any(), any());
        Object execute = mock.execute(null, null, "select", null);
        Assert.assertNull(execute);
    }


    WebDriver driver = mock(WebDriver.class);
    WebElement webElement = mock(WebElement.class);

    @BeforeClass
    public void setUp() {
        PageOptional.setWebDriver(driver);
    }

    @Test
    void clickTest() {
        Mockito.doNothing().when(webElement).click();
        try (MockedStatic<Wait> mockStatic = Mockito.mockStatic(Wait.class)) {
            when(Wait.waitElementClickable(webElement)).thenReturn(webElement);
            when(Wait.waitElementExist(any(), any())).thenReturn(webElement);
            PageOptional.waitForClick("#kw");
        }
    }

    @Test
    void actionsClickTest() {
        doReturn(null).when(mock(Actions.class)).contextClick();
        try (MockedStatic<Wait> mockStatic = Mockito.mockStatic(Wait.class)) {
            when(Wait.waitElementClickable(webElement)).thenReturn(webElement);
            when(Wait.waitElementExist(any(), any())).thenReturn(webElement);
            PageOptional.waitForRightClick("#kw");
            PageOptional.waitForDoubleClick("#kw");
        }
    }

    @Test(expectedExceptions = RuntimeException.class)
    void jsClickTest() {
        doReturn(null).when(mock(JavaScript.class)).execute(any(), any(Arrays.class));
        try (MockedStatic<Wait> mockStatic = Mockito.mockStatic(Wait.class)) {
            when(Wait.waitElementClickable(webElement)).thenReturn(webElement);
            when(Wait.waitElementExist(any(), any())).thenReturn(webElement);
            PageOptional.waitForJSClick("#kw");
        }

    }

    @Test
    void typeTest() {
        doNothing().when(webElement).sendKeys(any());
        try (MockedStatic<Wait> mockStatic = Mockito.mockStatic(Wait.class)) {
            when(Wait.waitElementClickable(webElement)).thenReturn(webElement);
            when(Wait.waitElementExist(any(), any())).thenReturn(webElement);
            PageOptional.waitForType("#kw", "test");
            doReturn("input").when(webElement).getTagName();
            PageOptional.waitForType("#kw", "test");
            PageOptional.waitForClickAndType("#s","1123");
        }
    }


    @Test(enabled = false)
    void selectTest() throws Exception {

        try (MockedStatic<Wait> mockStatic = Mockito.mockStatic(Wait.class)) {
            when(Wait.waitElementClickable(webElement)).thenReturn(webElement);
            when(Wait.waitElementExist(any(), any())).thenReturn(webElement);
            PageOptional.waitForSelect("#kw", "test");
//            PageOptional.waitForSelect("#kw", 0);
//            PageOptional.waitForClickSelect("#ss","test");
//            doReturn("select").when(webElement).getTagName();
//            PageOptional.waitForSelect("#kw", "test");
//            PageOptional.waitForSelect("#kw", 0);
        } catch (RuntimeException e) {
            Assert.assertEquals(e.getCause().getClass(), NoSuchElementException.class,e.getMessage());
        }
    }

    @Test
    void getValueTest() {
        doReturn("success").when(webElement).getAttribute(any());
        try (MockedStatic<Wait> mockStatic = Mockito.mockStatic(Wait.class)) {
            when(Wait.waitElementClickable(webElement)).thenReturn(webElement);
            when(Wait.waitElementExist(any(), any())).thenReturn(webElement);
            String value = PageOptional.getElementAttributeForValue("#kw", "test");
            Assert.assertEquals(value, "success");
        }
    }


    @Test(enabled = false)
    void setValueTest() {
        try (MockedStatic<Wait> mockStatic = Mockito.mockStatic(Wait.class)) {
            when(Wait.waitElementClickable(webElement)).thenReturn(webElement);
            when(Wait.waitElementExist(any(), any())).thenReturn(webElement);
            PageOptional.waitForSetAttribute("#kw", "value", "test");
            PageOptional.waitForSetValue("#kw", "test");
        }
    }

}