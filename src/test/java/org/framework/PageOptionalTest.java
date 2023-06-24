package org.framework;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class PageOptionalTest {

    @Test
    public void test() throws InterruptedException {
        PageOptional.open("https://www.baidu.com");
        PageOptional.waitForType("#kw", "java");
        PageOptional.waitForClick("#su");
        PageOptional.getWebElement("#s_btn_wr").findElement(By.xpath(" following-sibling::*[1]"));
    }

}