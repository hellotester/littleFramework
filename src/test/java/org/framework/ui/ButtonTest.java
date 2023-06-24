package org.framework.ui;

import org.framework.PageOptional;
import org.framework.ElementProxy;
import org.framework.WebElementFinder;
import org.testng.annotations.Test;

import java.lang.reflect.Proxy;

public class ButtonTest {

    @Test
    public void testWaitforClick() {
        PageOptional.open("http://www.baidu.com");
        PageOptional.waitForClick("#su");
    }

    @Test
    public void testWaitforRightClick() {
    }

    @Test
    public void testWaitforDoubleClick() {
    }

    @Test
    public void testWaitforJSClick() {
    }

    @Test
    public void testShould() {
    }


}